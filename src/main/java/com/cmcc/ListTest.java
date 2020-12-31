package com.cmcc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListTest {

	public static void main(String[] args) {
		List<String> strList = new ArrayList<>();
		for(int i=0;i<10000;i++) {
			strList.add("asdd");
		}
		strList.add("asdd");
		strList.add("asdddd");
		strList.add("asdd");
		strList.add("asdd");
		strList.add("asddaaaa");
		strList.add("asddxxxx");
		
		String path = "1";
		System.out.println("path:==="+path.split("1"+"").length);
		
		List<String> result = ListTest.getDuplicateElements(strList);
		System.out.println(result.size());
		
		for(String duliptr: result) {
			System.out.println(duliptr);
		}

	}
	
	public static <E> List<E> getDuplicateElements(List<E> list) {
        return (List<E>) list.stream()
                .collect(Collectors.toMap(e -> e, e -> 1, (a, b) -> a + b)) 
                .entrySet().stream() 
                .filter(entry -> entry.getValue() > 1)
                .map(entry -> entry.getKey()) 
                .collect(Collectors.toList()); 
    }

}
