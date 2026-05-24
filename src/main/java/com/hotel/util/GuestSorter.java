package com.hotel.util;

import com.hotel.model.Guest;
import java.util.Arrays;
import java.util.List;

public class GuestSorter {

    public enum SortBy {
        NAME,
        LOYALTY_POINTS,
        TYPE,
        EMAIL,
        ID
    }

    public static List<Guest> sort(List<Guest> list, SortBy sortBy) {
        if (list == null || list.size() <= 1) return list;
        Guest[] arr = list.toArray(new Guest[0]);
        quickSort(arr, 0, arr.length - 1, sortBy);
        return Arrays.asList(arr);
    }

    private static void quickSort(Guest[] arr, int low, int high, SortBy sortBy) {
        if (low < high) {
            int pivotIdx = partition(arr, low, high, sortBy);
            quickSort(arr, low, pivotIdx - 1, sortBy);
            quickSort(arr, pivotIdx + 1, high, sortBy);
        }
    }

    private static int partition(Guest[] arr, int low, int high, SortBy sortBy) {
        Guest pivot = arr[high];
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

    private static int compare(Guest a, Guest b, SortBy sortBy) {
        switch (sortBy) {
            case LOYALTY_POINTS:
                return Integer.compare(getPoints(a), getPoints(b));
            case TYPE:
                return compareStrings(getType(a), getType(b));
            case EMAIL:
                return compareStrings(getEmail(a), getEmail(b));
            case ID:
                return compareStrings(getId(a), getId(b));
            case NAME:
            default:
                return compareStrings(getName(a), getName(b));
        }
    }

    private static int compareStrings(String a, String b) {
        if (a == null && b == null) return 0;
        if (a == null) return 1;
        if (b == null) return -1;
        return a.compareToIgnoreCase(b);
    }

    private static String getName(Guest g) { return g == null ? null : g.getName(); }
    private static String getEmail(Guest g) { return g == null ? null : g.getEmail(); }
    private static String getId(Guest g) { return g == null ? null : g.getId(); }
    private static int getPoints(Guest g) { return g == null ? 0 : g.getLoyaltyPoints(); }
    private static String getType(Guest g) { return g == null ? null : g.getGuestType(); }

    private static void swap(Guest[] arr, int i, int j) {
        Guest tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp;
    }
}