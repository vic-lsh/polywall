/**
 * Utility file to convert benchmark results to CSV format.
 */

const fs = require("fs");
const { Parser } = require("json2csv");

/** The file where benchmark is located. */
const FILE_NAME = "../bench_persisted/results_193285383091666__10m.json";

let rawdata = fs.readFileSync(FILE_NAME);
let benchmarkObject = JSON.parse(rawdata);
console.log(benchmarkObject);

const benchmark = benchmarkObject["results"];

const workloadResults = Object.entries(benchmark);

const extractSpeedup = (threadCount, firewallName) => {
  return results[threadCount].results[firewallName].speedup;
};

workloadResults.forEach(([presetName, { results }]) => {
  const numThreads = Object.keys(results);
  const firewallDriverNames = Object.keys(results[numThreads[0]].results);

  // reformat data so that each row contains firewall speedup under different threads.
  const rows = firewallDriverNames.map((firewallName) => {
    const speedups = numThreads.reduce(
      (acc, threadCount) => ({
        ...acc,
        [threadCount]: extractSpeedup(threadCount, firewallName),
      }),
      {}
    );
    return { firewallName, ...speedups };
  });

  const parser = new Parser({ fields: ["firewallName", ...numThreads] });
  const csvContent = parser.parse(rows);
  fs.writeFileSync(`../bench_parsed/${presetName}_out.csv`, csvContent);
});
