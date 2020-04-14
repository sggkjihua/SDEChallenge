## Scenario

User query the flow of their website, based on time sequence, minimum span is per minute

## Requirement 

1. Billions of write / Day

   10,000,000,000/**day** = 41666666/**hour** = 694444/**min** = 11574/**second**

2. Handle large read/query volume: Millions of merchants wish to gain insight into their business. Read/Query patterns are time-series related metrics.

3. Provide metrics to customers with at most one hour delay.

4. Run with minimum downtime.

5. Have the ability to reprocess historical data in case of bugs in the processing logic

## Vertical Sharding for read/write request

Websites will be divided into different shards, each shard will be processed by a group of servers.

A **group** is:

- A Master-Follower pattern with **odd total number** of servers and **one** single master
- All servers will eventually come into a same result (**replica** of master)
- Master will commit a write only when a majority of servers in the group has confirmed accepting the request

If we choose an appropriate hashing technique, we can ideally divide **all the requesting websites evenly into different shards**. And each shard will have an associated group. If we define 10 shards, and 5 server groups, and if the algorithm is ideally designed, we can have **each group responsible for 2 shards**. Thus the write request will be as lower as 11574/5 ≈ 2314/**second**

A write will simply append a log in a file like:

| 2020.03.14 07:20:39.595 taobao.com |
| ---------------------------------- |
| 2020.03.14 07:25:42.412 tmall.com  |
| ...                                |
|                                    |

## Map-Reduce for processing the **Query** request 

Once the file is large enough or timeout (**1 hour**), a group could start processing the file. This could be done by Map-Reduce.

The master will divide the file evenly into all **other servers** in the group. Each server is responsible for dealing with a certain block of the file. The intermediate result of **one server** will be stored in format:

server1-key1, server1-key2, server1-key3...

which means all website will be **rehashed** again into different keys. A key might correspond to several websites but each website will have one unique key only. For example all records of **taobao.com** will lie in server1-key1, server2-key1, server3-key1...

After the **map** work is done. Master will then send a request to all servers to process the intermediate result. This time a server will be responsible for dealing with a certain key file. For instance, server1 will process all intermediate files **end with key1**, sorted by time. Output of this process will thus produce number of key files and will send the result back to master once finished.

On receiving the results from all servers, master will persist the result in database.

## Redis for caching

Same request might be issuing within one hour, we thus can utilize redis to cache the result so as not to bother the master again. Cache will be updated by the update of the database or hourly. As the record of the website is **sorted by time sequence** and **put together** after the **Map-Reduce** work. Querying for the result will be much easier.

## Ability for changing the logic

We store the row data sorted by time sequence only, thus reusing the records for other logic will be possible

