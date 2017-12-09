package com.weixin.utils;

import java.util.ArrayList;
import java.util.List;

class Solution {
    public int[] twoSum(int[] nums, int target) {
    	List<Integer> arr=new ArrayList<Integer>();
        for(int i=1;i<nums.length;i++){
            for(int j=0;j<i;j++){
                if(nums[i]+nums[j]==target)   {
                    System.out.println("下标"+(j)+":"+nums[j]+"\t下标"+i+":"+nums[i]);
                    arr.add(j);
                    arr.add(i);
                }             
            }
        }
        Object[] ret=arr.toArray();
        int[] rei=new int[ret.length];
        for (int i=0;i<ret.length;i++) {
			rei[i]=(Integer) ret[i];
		}
        return rei;
    }
    
    public static void main(String[] args) {
		Solution s=new Solution();
		int[] arr=new int[]{1,7,3,4,5,6,2,3};
		int[] ret=s.twoSum(arr,6);
		for (int object : ret) {
			System.out.println(object);
		}
	}
    
}