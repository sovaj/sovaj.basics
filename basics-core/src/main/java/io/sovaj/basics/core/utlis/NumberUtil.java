package io.sovaj.basics.core.utlis;

import org.apache.commons.lang3.RandomStringUtils;

/**
 *
 * @author Sebastien Dionne
 */
public class NumberUtil {

    /**
     * Generate a random number
     *
     * @param length - The length of the random string to generate
     * @return The random string
     */
    public static String generateRandomNumber(int length) {
        if (length <= 0) {
            throw new RuntimeException("Invalid length : must be greater than 0");
        }

        String newMerchantId = RandomStringUtils.randomNumeric(length);
        if (newMerchantId.startsWith("0")) {
            newMerchantId = "1" + newMerchantId.substring(1);
        }

        return newMerchantId;
    }

}
