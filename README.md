# Airports Data RESTful API

_Airports data from http://ourairports.com/data/ (released under public domain)_

```
GET /api/search/<query>
```

If `<query>` is a runway identity prepended with a `$` it returns the runway data.

If `<query>` is a 2 character string it returns the first page of airports (10) from
country with country_code equal to the query string.

Else it does a fuzzy search over the country names and keywords.

```
GET /api/countries/most
```

Returns an array with the top 10 countries with airports and its airports count.

```
GET /api/countries/least
```

Returns all the countries with 1 airport (1 airport is the least amount of airports in a country).

```
GET /api/countries/<page>
```

Returns pagination of countries. Page size is 10.

```
GET /api/airport/<country_code>/runways
```

Returns all runways of country with code `<country_code>`

```
GET /api/latitudes/most
```

Returns the top 10 runway latitudes.

```
GET /api/<country_code>/airports
```

Returns all the airports of country with code `<country_code>`

```
GET /api/<country_code>/airports/<page>
```

Returns pagination of airports of country with code `<country_code>`. Page size is 10.
