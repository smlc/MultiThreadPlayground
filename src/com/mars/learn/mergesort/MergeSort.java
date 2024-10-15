package com.mars.learn.mergesort;

import java.util.Arrays;

public class MergeSort {

    private static int[] scratch = new int[10];
    public static void main(String args[]) {
        int[] input = new int[]{9, 8, 7, 6, 5, 4, 3, 2, 1, 0};
        System.out.println(Arrays.toString(input));
        mergeSort(0, input.length - 1, input);
        System.out.println(Arrays.toString(input));
    }

    private static void mergeSort(int start, int end, int[] input) {
        if(start==end) return;

        int mid = start + ((end - start) / 2);
        mergeSort(start, mid , input);
        mergeSort(mid + 1, end, input);
        int i = start;
        int j = mid + 1;
        int k;

        for (k = start; k <= end; k++) {
            scratch[k] = input[k];
        }

        k = start;
        while (k <= end) {

            if (i <= mid && j <= end) {
                input[k] = Math.min(scratch[i], scratch[j]);

                if (input[k] == scratch[i]) {
                    i++;
                } else {
                    j++;
                }
            } else if (i <= mid && j > end) {
                input[k] = scratch[i];
                i++;
            } else {
                input[k] = scratch[j];
                j++;
            }
            k++;

        }

    }
}
