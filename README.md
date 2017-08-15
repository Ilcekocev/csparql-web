Csparql WEB

Simple test:
* Create a user

```
curl --request POST \
    --url http://localhost:8080/api/user \
    --header 'content-type: application/json' \
    --data '{
  	"username": "test",
  	"token": "fcm-token"
  } '
```
* Register a query

```
curl --request POST \
  --url http://localhost:8080/api/user/test/query \
  --header 'content-type: application/json' \
  --data '{
	"definition": "REGISTER QUERY Locations AS PREFIX ex: <http://myexample.org/> PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> SELECT ?s ?p ?o FROM STREAM <http://myexample.org/stream> [RANGE 10s STEP 2s] WHERE { ?s ?p ?o . }"
} '
```
* Start adding events

```
curl --request POST \
  --url http://localhost:8080/api/event \
  --header 'content-type: application/json' \
  --data '{
	"subject": "some-subject",
	"predicate": "some-predicate",
	"object": "some-object"
}'
```