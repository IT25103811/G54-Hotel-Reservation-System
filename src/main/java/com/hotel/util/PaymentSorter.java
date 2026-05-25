package com.hotel.util;

import com.hotel.model.Payment;
import java.util.Arrays;
import java.util.List;

/**
 * QuickSort-based sorter for {@link Payment} objects.
 *
 * Provides in-place O(n log n) average-case sorting across multiple
 * sort keys, replacing any ad-hoc stream().sorted() calls so that the
 * same algorithm is used consistently throughout the payment module.
 *
 * Supported sort criteria:
 *  - AMOUNT_ASC   : lowest amount first
 *  - AMOUNT_DESC  : highest amount first
 *  - DATE_ASC     : earliest timestamp first
 *  - DATE_DESC    : most recent timestamp first
 *  - STATUS       : alphabetical by status name (PAID → PENDING → VOIDED)
 *  - PAYMENT_ID   : lexicographic ascending by paymentId
 */
public class PaymentSorter {

    /** Sort criteria available for payment lists. */
    public enum SortBy {
        AMOUNT_ASC,
        AMOUNT_DESC,
        DATE_ASC,
        DATE_DESC,
        STATUS,
        PAYMENT_ID
    }

    /**
     * Sort a list of payments using QuickSort and return the sorted list.
     *
     * @param list   the payments to sort (may be {@code null} or empty)
     * @param sortBy the criterion to sort by
     * @return the sorted list (same list reference if size ≤ 1)
     */
    public static List<Payment> sort(List<Payment> list, SortBy sortBy) {
        if (list == null || list.size() <= 1) return list;
        Payment[] arr = list.toArray(new Payment[0]);
        quickSort(arr, 0, arr.length - 1, sortBy);
        return Arrays.asList(arr);
    }

    // ── QuickSort core ────────────────────────────────────────────────────────

    private static void quickSort(Payment[] arr, int low, int high, SortBy sortBy) {
        if (low < high) {
            int pivotIdx = partition(arr, low, high, sortBy);
            quickSort(arr, low,          pivotIdx - 1, sortBy);
            quickSort(arr, pivotIdx + 1, high,         sortBy);
        }
    }

    /**
     * Lomuto partition scheme: places pivot in its final sorted position and
     * returns that index.
     */
    private static int partition(Payment[] arr, int low, int high, SortBy sortBy) {
        Payment pivot = arr[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (compare(arr[j], pivot, sortBy) <= 0) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    // ── Comparator ────────────────────────────────────────────────────────────

    private static int compare(Payment a, Payment b, SortBy sortBy) {
        switch (sortBy) {

            case AMOUNT_ASC:
                return Double.compare(a.getAmount(), b.getAmount());

            case AMOUNT_DESC:
                return Double.compare(b.getAmount(), a.getAmount()); // reversed

            case DATE_ASC:
                if (a.getTimestamp() == null && b.getTimestamp() == null) return 0;
                if (a.getTimestamp() == null) return 1;  // nulls last
                if (b.getTimestamp() == null) return -1;
                return a.getTimestamp().compareTo(b.getTimestamp());

            case DATE_DESC:
                if (a.getTimestamp() == null && b.getTimestamp() == null) return 0;
                if (a.getTimestamp() == null) return 1;  // nulls last
                if (b.getTimestamp() == null) return -1;
                return b.getTimestamp().compareTo(a.getTimestamp()); // reversed

            case STATUS:
                if (a.getStatus() == null && b.getStatus() == null) return 0;
                if (a.getStatus() == null) return 1;
                if (b.getStatus() == null) return -1;
                return a.getStatus().name().compareTo(b.getStatus().name());

            case PAYMENT_ID:
                if (a.getPaymentId() == null && b.getPaymentId() == null) return 0;
                if (a.getPaymentId() == null) return 1;
                if (b.getPaymentId() == null) return -1;
                return a.getPaymentId().compareTo(b.getPaymentId());

            default:
                return 0;
        }
    }

    private static void swap(Payment[] arr, int i, int j) {
        Payment tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
}
