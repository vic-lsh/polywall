# multsync-capstone

Capstone project for CS1760 Multiprocessor Synchronization @ Brown.

## Getting started

This project is developed using maven 3.8.3 and Java 17.0.1.

```
mvn package 		# install dependencies
mvn test			# run all tests
mvn exec:java       # run benchmarking code
```

## Sample benchmarking results

See `packetfilter/bench_persisted/results_193285383091666__10m.json` for the
benchmarking results used in the final report. Run `mvn exec:java` to re-run
benchmarks.
