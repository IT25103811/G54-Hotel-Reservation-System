package com.hotel.util;

import com.hotel.model.Room;
import java.util.Arrays;
import java.util.List;

/**
 * QuickSort-based sorter for {@link Room} objects.
 *
 * Provides in-place O(n log n) average-case sorting across several
 * sort keys, replacing scattered stream().sorted() calls so that the
 * same algorithm is used consistently throughout the room module.
 *
 * Supported sort criteria:
 *  - PRICE_ASC      : lowest price first
 *  - PRICE_DESC     : highest price first
 *  - ROOM_NUMBER    : lexicographic ascending (101, 102, 201, …)
 *  - FLOOR_ASC      : lowest floor first
 *  - FLOOR_DESC     : highest floor first
 *  - TYPE           : alphabetical by room type
 *  - AVAILABILITY   : available rooms first, then unavailable
 */
public class RoomSorter {

    /** Sort criteria available for room lists. */
    public enum SortBy {
        PRICE_ASC,
        PRICE_DESC,
        ROOM_NUMBER,
        FLOOR_ASC,
        FLOOR_DESC,
        TYPE,
        AVAILABILITY
    }

    /**
     * Sort a list of rooms using QuickSort and return the sorted list.
     *
     * @param list   the rooms to sort (may be {@code null} or empty)
     * @param sortBy the criterion to sort by
     * @return the sorted list (same list reference if size ≤ 1)
     */
    public static List<Room> sort(List<Room> list, SortBy sortBy) {
        if (list == null || list.size() <= 1) return list;
        Room[] arr = list.toArray(new Room[0]);
        quickSort(arr, 0, arr.length - 1, sortBy);
        return Arrays.asList(arr);
    }

    // ── QuickSort core ────────────────────────────────────────────────────────

    private static void quickSort(Room[] arr, int low, int high, SortBy sortBy) {
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
    private static int partition(Room[] arr, int low, int high, SortBy sortBy) {
        Room pivot = arr[high];
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

    private static int compare(Room a, Room b, SortBy sortBy) {
        switch (sortBy) {

            case PRICE_ASC:
                return Double.compare(a.getPrice(), b.getPrice());

            case PRICE_DESC:
                return Double.compare(b.getPrice(), a.getPrice()); // reversed

            case ROOM_NUMBER:
                if (a.getRoomNumber() == null && b.getRoomNumber() == null) return 0;
                if (a.getRoomNumber() == null) return 1;
                if (b.getRoomNumber() == null) return -1;
                return a.getRoomNumber().compareTo(b.getRoomNumber());

            case FLOOR_ASC:
                return Integer.compare(a.getFloor(), b.getFloor());

            case FLOOR_DESC:
                return Integer.compare(b.getFloor(), a.getFloor()); // reversed

            case TYPE:
                if (a.getType() == null && b.getType() == null) return 0;
                if (a.getType() == null) return 1;
                if (b.getType() == null) return -1;
                return a.getType().compareToIgnoreCase(b.getType());

            case AVAILABILITY:
                // true (available) sorts before false (unavailable)
                return Boolean.compare(!a.isAvailable(), !b.isAvailable());

            default:
                return 0;
        }
    }

    private static void swap(Room[] arr, int i, int j) {
        Room tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
}
