package com.hotel.util;

import com.hotel.model.Reservation;
import java.util.Arrays;
import java.util.List;


public class ReservationSorter {

    public enum SortBy {
        CHECK_IN,     // check-in date ascending
        CHECK_OUT,    // check-out date ascending
        TOTAL_AMOUNT, // amount ascending
        STATUS        // status name alphabetical
    }


    public static List<Reservation> sort(List<Reservation> list, SortBy sortBy) {
        if (list == null || list.size() <= 1) return list;
        Reservation[] arr = list.toArray(new Reservation[0]);
        quickSort(arr, 0, arr.length - 1, sortBy);
        return Arrays.asList(arr);
    }

    // ── QuickSort ─────────────────────────────────────────────────────────────

    private static void quickSort(Reservation[] arr, int low, int high, SortBy sortBy) {
        if (low < high) {
            int pivotIdx = partition(arr, low, high, sortBy);
            quickSort(arr, low, pivotIdx - 1, sortBy);
            quickSort(arr, pivotIdx + 1, high, sortBy);
        }
    }


    private static int partition(Reservation[] arr, int low, int high, SortBy sortBy) {
        Reservation pivot = arr[high];
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


    private static int compare(Reservation a, Reservation b, SortBy sortBy) {
        switch (sortBy) {
            case CHECK_IN:
                if (a.getCheckIn() == null && b.getCheckIn() == null) return 0;
                if (a.getCheckIn() == null) return 1;
                if (b.getCheckIn() == null) return -1;
                return a.getCheckIn().compareTo(b.getCheckIn());
            case CHECK_OUT:
                if (a.getCheckOut() == null && b.getCheckOut() == null) return 0;
                if (a.getCheckOut() == null) return 1;
                if (b.getCheckOut() == null) return -1;
                return a.getCheckOut().compareTo(b.getCheckOut());
            case TOTAL_AMOUNT:
                return Double.compare(a.getTotalAmount(), b.getTotalAmount());
            case STATUS:
                if (a.getStatus() == null && b.getStatus() == null) return 0;
                if (a.getStatus() == null) return 1;
                if (b.getStatus() == null) return -1;
                return a.getStatus().name().compareTo(b.getStatus().name());
            default:
                return 0;
        }
    }

    private static void swap(Reservation[] arr, int i, int j) {
        Reservation tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp;
    }
}
