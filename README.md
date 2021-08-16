## Riot Games Interview Assignment
This is sample Create/Read/Update/Delete APIs for Dashboard application.

## Implementation Details
### Environment
* SpringBoot 2.5.3
* Java 11

### Consideration
1. This API server is backed by in-memory storage, so data will be reset at every server restart.
1. This API server is considered thread-safe.
1. This API server consists of controller-server-repository components.
1. This API server doesn't break data consistency.
1. This API server is not scalable due to in-memory storage.

### Assumptions
1. Ranking related traffic is way more than other traffic, so it needs to weigh efficiency of ranking related operations.
1. Players' rank is not stored in the storage, but calculated at every request. Every other player properties like ID, MMR, and Tier are persisted in the storage.
1. Once a player's tier is decided and persisted, operations on other players cannot change it. From this assumption, the number of players of each tier is allowed to exceed the limit transiently.
    * However, Challenger players will be demoted to Master when their rank is below 100. This demotion is assumed to be done by a sort of daily batch processes, which is not implemented in this application.
1. Ranking considering factors are Tier and MMR in that order. PlayerID is used as a tiebreaker.


## Commands
### Test
```
./gradlew test
```

### Build
```
./gradlew build

# Result path will be build/libs/dashboard-api-{version}.jar
```

### Run
```
# Run using Gradle
./gradlew bootrun

# Run using JAR (Should be after build)
java -server -jar build/libs/dashboard-api-{version}.jar
```

## APIs
1. Get total players count
    ```
    curl localhost:8080/dashboard/v1/players/total-count
    ```

1. Get player's tier
    ```
    curl localhost:8080/dashboard/v1/player/{playerID}/tier
   
    # Example
    curl localhost:8080/dashboard/v1/player/13309/tier
    ```

1. Get top 10 players
    ```
    curl localhost:8080/dashboard/v1/players/top-10
    ```

1. Get near N players around given player
    ```
    curl localhost:8080/dashboard/v1/player/{pivotPlayerID}/near/{range}
    
    # Example
    curl localhost:8080/dashboard/v1/player/16472/near/5
    ```
   
1. Create new player
    ```
    # Example
    curl -X POST localhost:8080/dashboard/v1/player/create \
        -H "Content-Type: application/json" \
        -d "{
                \"mmr\": 4000
            }"
    ```

1. Update player
    ```
    # Example
    curl -X POST localhost:8080/dashboard/v1/player/update \
        -H "Content-Type: application/json" \
        -d "{
                \"playerID\": 13309,
                \"mmr\": 5000
            }"
    ```
   
1. Delete player
    ```
    curl -i -X DELETE localhost:8080/dashboard/v1/player/{playerID}
   
    # Example
    curl -i -X DELETE localhost:8080/dashboard/v1/player/5720
    ```
