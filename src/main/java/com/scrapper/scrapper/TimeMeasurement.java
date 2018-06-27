package com.scrapper.scrapper;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

public class TimeMeasurement {

	private static Map<String, List<Long>> times = new HashMap<>();
	private static final Logger LOG = Logger.getLogger(TimeMeasurement.class);

	public static <T> T measureTimeAndExecute(String methodIdentifier, Callable<T> task) {
		T call = null;
		try {
			Instant start = Instant.now();
			call = task.call();
			Instant end = Instant.now();
			addTime(methodIdentifier, Duration.between(start, end).toMillis());
		} catch (Exception e) {
			LOG.error("error while calling task");
		}
		return call;
	}

	private static void addTime(String methodIdentifier, long timeInMillis) {
		if (times.containsKey(methodIdentifier)) {
			times.get(methodIdentifier).add(timeInMillis);

		} else {
			List<Long> timesForIdentifier = new ArrayList<>();
			timesForIdentifier.add(timeInMillis);
			times.put(methodIdentifier, timesForIdentifier);
		}
	}

	public static void measureTimeAndExecute(String methodIdentifier, Runnable task) {
		Instant start = Instant.now();
		task.run();
		Instant end = Instant.now();
		addTime(methodIdentifier, Duration.between(start, end).toMillis());
	}

	public static void printTimes() {
		System.out.println("---Avarage times---");
		times.entrySet().forEach(e -> {
			OptionalDouble avarageTime = e.getValue().stream().mapToLong(Long::longValue).average();
			String output = new StringBuilder(e.getKey()).append(":").append(avarageTime.getAsDouble()).append("ms")
					.toString();
			System.out.println(output);
		});
	}

}