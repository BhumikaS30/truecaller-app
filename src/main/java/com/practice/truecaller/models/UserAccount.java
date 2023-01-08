package com.practice.truecaller.models;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.practice.truecaller.exceptions.BlockContactsExceededException;
import com.practice.truecaller.exceptions.ContactsExceededException;
import com.practice.truecaller.models.common.Address;
import com.practice.truecaller.models.common.Contact;
import com.practice.truecaller.models.common.GlobalSpam;
import com.practice.truecaller.models.common.PersonalInfo;
import com.practice.truecaller.models.common.SocialInfo;
import com.practice.truecaller.models.common.Tag;
import com.practice.truecaller.models.tries.Trie;

import org.checkerframework.checker.units.qual.C;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import orestes.bloomfilter.CountingBloomFilter;
import orestes.bloomfilter.FilterBuilder;

import static com.practice.truecaller.models.common.Constant.MAX_GOLD_USER_BLOCKED_CONTACTS;
import static com.practice.truecaller.models.common.Constant.MAX_GOLD_USER_CONTACTS;
import static com.practice.truecaller.models.common.Constant.MAX_PLATINUM_USER_BLOCKED_CONTACTS;
import static com.practice.truecaller.models.common.Constant.MAX_PLATINUM_USER_CONTACTS;
import static com.practice.truecaller.models.common.Constant.MAX_STANDARD_USER_BLOCKED_CONTACTS;
import static com.practice.truecaller.models.common.Constant.MAX_STANDARD_USER_CONTACTS;

@Getter
@Setter
public class UserAccount {

    private String id;

    private String phoneNumber;

    private String userName;

    private String password;

    private LocalDateTime lastAccessed;

    private Tag tag;

    private Contact contact;

    private PersonalInfo personalInfo;

    private Business business;

    private Address address;

    private SocialInfo socialInfo;

    private UserCategory userCategory;

    private Map<String, UserAccount> contacts;

    private CountingBloomFilter<String> blockedContacts;

    private Set<String> blockedSet;

    private Trie contactTrie;

    public UserAccount() {
        contactTrie = new Trie();
    }

    public UserAccount(String phoneNumber, String firstName) {
        this.phoneNumber = phoneNumber;
        this.personalInfo = new PersonalInfo(firstName);
    }

    public UserAccount(String phoneNumber, String firstName, String lastName) {
        this.phoneNumber = phoneNumber;
        this.personalInfo = new PersonalInfo(firstName, lastName);
    }

    public void register(UserCategory userCategory, String userName, String password, String email,
                         String phoneNumber, String countryCode, String firstName) {
        setId(UUID.randomUUID().toString());
        setUserName(userName);
        setUserCategory(userCategory);
        setPassword(password);
        setContact(new Contact(phoneNumber, email, countryCode));
        setPhoneNumber(phoneNumber);
        setPersonalInfo(new PersonalInfo(firstName));
        init(userCategory);
        insertIntoTries(phoneNumber, getPersonalInfo().getFirstName());
    }

    private void init(UserCategory userCategory) {
        switch (userCategory) {
            case STANDARD:
                setContacts(new HashMap<>(MAX_STANDARD_USER_CONTACTS));
                setBlockedContacts(new FilterBuilder(MAX_STANDARD_USER_BLOCKED_CONTACTS, .01)
                                       .buildCountingBloomFilter());
                setBlockedSet(new HashSet<>(MAX_STANDARD_USER_BLOCKED_CONTACTS));
                break;
            case GOLD:
                setContacts(new HashMap<>(MAX_GOLD_USER_CONTACTS));
                setBlockedContacts(new FilterBuilder(MAX_GOLD_USER_BLOCKED_CONTACTS, .01)
                                       .buildCountingBloomFilter());
                setBlockedSet(new HashSet<>(MAX_GOLD_USER_BLOCKED_CONTACTS));
                break;

            case PLATINUM:
                setContacts(new HashMap<>(MAX_PLATINUM_USER_CONTACTS));
                setBlockedContacts(new FilterBuilder(MAX_PLATINUM_USER_BLOCKED_CONTACTS, .01)
                                       .buildCountingBloomFilter());
                setBlockedSet(new HashSet<>(MAX_PLATINUM_USER_BLOCKED_CONTACTS));
                break;
        }
    }

    public void addContacts(UserAccount userAccount) throws ContactsExceededException {
        checkAddContactsLimit();
        getContacts().putIfAbsent(userAccount.getPhoneNumber(), userAccount);
        insertIntoTries(userAccount.getPhoneNumber(), userAccount.getPersonalInfo().getFirstName());
    }

    private void insertIntoTries(String phoneNumber, String firstName) {
        getContactTrie().insertInTrie(phoneNumber);
        getContactTrie().insertInTrie(firstName);
        GlobalContacts.INSTANCE.getContactTrie().insertInTrie(phoneNumber);
        GlobalContacts.INSTANCE.getContactTrie().insertInTrie(firstName);
    }

    private void checkAddContactsLimit() throws ContactsExceededException {
        switch (this.getUserCategory()) {
            case STANDARD:
                if (this.getContacts().size() >= MAX_STANDARD_USER_CONTACTS) {
                    throw new ContactsExceededException("Default contact size exceeded");
                }
            case GOLD:
                if (this.getContacts().size() >= MAX_GOLD_USER_CONTACTS) {
                    throw new ContactsExceededException("Default contact size exceeded");
                }
            case PLATINUM:
                if (this.getContacts().size() >= MAX_PLATINUM_USER_CONTACTS) {
                    throw new ContactsExceededException("Default contact size exceeded");
                }
        }
    }

    public void blockNumber(String blockNumber) throws BlockContactsExceededException {
        checkBlockContactsLimit();
        getBlockedContacts().add(blockNumber);
    }

    private void checkBlockContactsLimit() throws BlockContactsExceededException {
        switch (this.getUserCategory()) {
            case STANDARD:
                if (this.getContacts().size() >= MAX_STANDARD_USER_BLOCKED_CONTACTS) {
                    throw new BlockContactsExceededException("Default block contact size exceeded");
                }
            case GOLD:
                if (this.getContacts().size() >= MAX_GOLD_USER_BLOCKED_CONTACTS) {
                    throw new BlockContactsExceededException("Default block contact size exceeded");
                }
            case PLATINUM:
                if (this.getContacts().size() >= MAX_PLATINUM_USER_BLOCKED_CONTACTS) {
                    throw new BlockContactsExceededException("Default block contact size exceeded");
                }
        }
    }

    public  void reportSpam(String phoneNumber, String reason) {
        getBlockedContacts().add(phoneNumber);
        GlobalSpam.INSTANCE.reportGlobalSpam(phoneNumber, this.phoneNumber, reason);
    }

    public  void unBlockNumbers(String phoneNumber) {
        getBlockedContacts().remove(phoneNumber);
    }

    public  void upgrade(UserCategory userCategory) {
        int contactsCount = 0;
        int blockedCount = 0;

        switch (userCategory) {
            case GOLD:
                contactsCount = MAX_GOLD_USER_CONTACTS;
                blockedCount = MAX_GOLD_USER_BLOCKED_CONTACTS;
                break;
            case PLATINUM:
                contactsCount = MAX_PLATINUM_USER_CONTACTS;
                blockedCount = MAX_PLATINUM_USER_BLOCKED_CONTACTS;
                break;
        }
        upgradeUserContacts(contactsCount);
        upgradeBlockedContacts(blockedCount);
    }

    private void upgradeUserContacts(int contactsCount) {
        Map<String, UserAccount> contacts = new HashMap<>(contactsCount);
        contacts.putAll(getContacts());
        setContacts(contacts);
    }

    private void upgradeBlockedContacts(int blockedCount) {
        setBlockedContacts(new FilterBuilder(blockedCount, .01).buildCountingBloomFilter());
        for (String blocked : getBlockedSet()) {
            getBlockedContacts().add(blocked);
        }
    }

    public boolean importContacts(List<UserAccount> users) {

        for (UserAccount userAccount: users) {
            try {
                addContacts(userAccount);
            } catch (ContactsExceededException e) {
                System.out.println("Some of the contact could not be imported as limit exceeded");
                return false;
            }
        }
        return true;
    }

    public boolean isBlocked(String phoneNumber) {
        return getBlockedContacts().contains(phoneNumber);
    }

    public boolean canReceive(String number) {
        return !isBlocked(number) &&
               !GlobalSpam.INSTANCE.isGlobalSpam(number);
    }

}
