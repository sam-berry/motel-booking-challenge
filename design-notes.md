# Booking System Design Notes

## Reservation Workflow

The reservation workflow for this system should follow the following steps: search, book, invoice. Search should begin by asking the user what their search criteria is. Based on that criteria, a set of inventory should be returned which communicates availability and pricing. The user can continue the process by submitting a booking request. If the booking is successful, an invoice should be returned to the user letting them know what will be due at the time of their visit.

## System Features

### Adding a Room

There needs to be a way to add a room to the system so that it is available to book.

**Required Information**

* Room number (string) <- must be unique
* Number of beds (integer)
* Handicap accessible (boolean)
* Building level (integer) <- metadata, not used in any backend logic
* Pet friendly (boolean)

### Max Pets Rule

There must be a mechanism that limits the number of pets on a reservation to 2.

**Required Information**

* Maximum number of pets allowed (integer)

### Pricing Rules

There needs to be a rule chain for determining pricing. Pricing is based on number of beds and number of pets.

#### Base Pricing

Base pricing is the base rate for a room based on the number of beds. There needs to be a way to add a base price.

**Required Information**

* Number of beds (integer)
* Price (money)

#### Pet Fee

A pet fee is the price per pet.

**Required Information**

* Fee amount (money)

### Availability Search

There needs to a mechanism for searching available rooms for a date range. Specific room numbers do not matter, just how many that fit the criteria. Two rooms with the same criteria are essentially the same to the customer - typically people do not care if they are in room 13 or 14 if they are set up exactly the same.

**Required Information**

* Start date (local date)
* End date (local date)
* Number of beds (integer)
* Number of pets (integer)
* Handicap accessible (boolean)

### Booking

There needs to be a way to book an available room for a period of time. Same as availability search, room number is not important here. The system should choose an available room based on the provided criteria.

**Required Information**

* Start date (local date)
* End date (local date)
* Number of beds (integer)
* Number of pets (integer)
* Handicap accessible (boolean)