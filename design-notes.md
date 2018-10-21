# Booking System Design Notes

## Adding a Room

There needs to be a way to add a room to the system so that it is available to book.

**Required Information**

* Room number (string) <- must be unique
* Number of beds (integer)
* Handicap accessible (boolean)
* Building level (integer)
* Pet friendly (boolean)

## Max Pets Rule

There must be a mechanism that limits the number of pets on a reservation to 2.

**Required Information**

* Maximum number of pets allowed (integer)

## Pricing Rules

There needs to be a rule chain for determining pricing. Pricing is based on number of beds and number of pets.

### Base Pricing

Base pricing is the base rate for a room based on the number of beds. There needs to be a way to add a base price.

**Required Information**

* Number of beds (integer)
* Price (money)

### Pet Fee

A pet fee is the price per pet.

**Required Information**

* Fee amount (money)

## Availability Search

There needs to a mechanism for searching available rooms for a date range.

**Required Information**

* Start date (local date)
* End date (local date)
* Number of pets (integer)
* Handicap accessible (boolean)

## Booking

There needs to be a way to book an available room for a period of time.

**Required Information**

* Room number (string)
* Start date (local date)
* End date (local date)

## Additional Thoughts

* It seems unnecessary for the system to build logic around the level number. Instead it should be based on the attributes. Level may be useful as metadata for searching. For example, a customers may prefer to stay on the 2nd level because they believe it is safer. But the system should not base handicap accessibility on level. What if an elevator is installed making all rooms handicap accessible? Better to use a separate indicator for this. Same idea for pet friendliness.

