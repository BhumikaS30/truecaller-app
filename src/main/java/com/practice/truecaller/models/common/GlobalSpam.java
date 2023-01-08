package com.practice.truecaller.models.common;

import java.nio.charset.Charset;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import lombok.NoArgsConstructor;
import orestes.bloomfilter.CountingBloomFilter;
import orestes.bloomfilter.FilterBuilder;

import static com.practice.truecaller.models.common.Constant.MAX_COUNT_TO_MARK_GLOBAL_BLOCKED;
import static com.practice.truecaller.models.common.Constant.MAX_GLOBAL_SPAM_COUNT;

@NoArgsConstructor
public class GlobalSpam {

    public static GlobalSpam INSTANCE = new GlobalSpam();

    private com.google.common.hash.BloomFilter<String> globalBlocked =
        BloomFilter.create(Funnels.stringFunnel(Charset.forName("UTF-8")),
                           MAX_GLOBAL_SPAM_COUNT);

    CountingBloomFilter<String> globalSpam =
        new FilterBuilder(MAX_GLOBAL_SPAM_COUNT, .01).buildCountingBloomFilter();

    public void reportGlobalSpam(String spamNumber, String reporterNumber, String reason) {

        System.out.println("Send metrics here for spam Number " + spamNumber +
                           " reported by " + reporterNumber + " for reason " + reason);

        if (globalSpam.getEstimatedCount(spamNumber) >= MAX_COUNT_TO_MARK_GLOBAL_BLOCKED) {
            globalBlocked.put(spamNumber);
        } else {
            globalSpam.add(spamNumber);
        }
    }

    public boolean isGlobalSpam(String spamNumber) {
        return globalSpam.contains(spamNumber);
    }

    public boolean isGloballyBlocked(String spamNumber) {
        return globalBlocked.mightContain(spamNumber);
    }
}
