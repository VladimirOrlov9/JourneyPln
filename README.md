# Android application for planning journeys

## Overview

This application allows you to create and track trips. For each trip, it is possible to add lists of things to collect, as well as a
to-do list for each trip. Also, this application allows you to search for hotels around.

## Technology stack

* Fragment Navigation
* Moxy
* Firebase
  * Firebase Authorization
  * Firebase Realtime Database
  * Firebase Storage
* Retrofit2
* Google Maps Places
* Picasso

## Used API

* Google Maps API
* OpenWeatherMap API

## Functional

### 1. Authorization page

On the authorization page, you can log in using your Google account.

<p float="left">
  <img src="https://github.com/VladimirOrlov9/JourneyPln/blob/master/screenshots/Screenshot_20210608-192153_JourneyPln.jpg" width="300" />
  <img src="https://github.com/VladimirOrlov9/JourneyPln/blob/master/screenshots/Screenshot_20210608-192201_Google%20Play%20services.jpg" width="300" /> 
</p>

### 2. Home page

The main page displays: current, nearest and last trips. In addition, it is possible to create a new trip.
The side menu is used for navigation.

<p float="left">
  <img src="https://github.com/VladimirOrlov9/JourneyPln/blob/master/screenshots/Screenshot_20210608-193219_JourneyPln.jpg" width="300" />
  <img src="https://github.com/VladimirOrlov9/JourneyPln/blob/master/screenshots/Screenshot_20210608-193715_JourneyPln.jpg" width="300" /> 
</p>

### 3. Creation new trip page

The photo is added in two ways: from the user's gallery or using the camera in the application. 
To select a destination, the Google Autocomplete service is used.

<p float="left">
  <img src="https://github.com/VladimirOrlov9/JourneyPln/blob/master/screenshots/Screenshot_20210608-195313_JourneyPln.jpg" width="300" />
  <img src="https://github.com/VladimirOrlov9/JourneyPln/blob/master/screenshots/Screenshot_20210608-195232_JourneyPln.jpg" width="300" />
  <img src="https://github.com/VladimirOrlov9/JourneyPln/blob/master/screenshots/Screenshot_20210608-195244_JourneyPln.jpg" width="300" />
</p>

### 4. Trip page

To display information about weather conditions at the destination, the OpenWeatherMap API is used.

<p float="left">
  <img src="https://github.com/VladimirOrlov9/JourneyPln/blob/master/screenshots/Screenshot_20210608-200837_JourneyPln.jpg" width="300" />
  <img src="https://github.com/VladimirOrlov9/JourneyPln/blob/master/screenshots/Screenshot_20210608-200854_JourneyPln.jpg" width="300" />
</p>

<p float="left">
  <img src="https://github.com/VladimirOrlov9/JourneyPln/blob/master/screenshots/Screenshot_20210608-205141_JourneyPln.jpg" width="300" />
  <img src="https://github.com/VladimirOrlov9/JourneyPln/blob/master/screenshots/Screenshot_20210608-205148_JourneyPln.jpg" width="300" />
</p>

<p float="left">
  <img src="https://github.com/VladimirOrlov9/JourneyPln/blob/master/screenshots/Screenshot_20210608-201701_JourneyPln.jpg" width="300" />
  <img src="https://github.com/VladimirOrlov9/JourneyPln/blob/master/screenshots/Screenshot_20210608-201715_JourneyPln.jpg" width="300" />
</p>

<p float="left">
  <img src="https://github.com/VladimirOrlov9/JourneyPln/blob/master/screenshots/Screenshot_20210608-205915_JourneyPln.jpg" width="300" />
  <img src="https://github.com/VladimirOrlov9/JourneyPln/blob/master/screenshots/Screenshot_20210608-210046_JourneyPln.jpg" width="300" />
</p>

### 5. View all trips page

The page is divided into 2 tabs: upcoming and last trips. To delete, use a swipe to the left.

<p float="left">
  <img src="https://github.com/VladimirOrlov9/JourneyPln/blob/master/screenshots/Screenshot_20210608-211051_JourneyPln.jpg" width="300" />
  <img src="https://github.com/VladimirOrlov9/JourneyPln/blob/master/screenshots/Screenshot_20210608-211058_JourneyPln.jpg" width="300" />
</p>

### 6. Nearby hotels search page

Markers are displayed on the map, by clicking on which the name, address and rating of the hotel are displayed.

<p float="left">
  <img src="https://github.com/VladimirOrlov9/JourneyPln/blob/master/screenshots/Screenshot_20210608-211519_JourneyPln.jpg" width="300" />
</p>

## Additional functions

The application supports Russian and English localization. Also, the app theme adjusts to the custom device theme.

<p float="left">
  <img src="https://github.com/VladimirOrlov9/JourneyPln/blob/master/screenshots/Screenshot_20210608-220439_JourneyPln.jpg" width="300" />
  <img src="https://github.com/VladimirOrlov9/JourneyPln/blob/master/screenshots/Screenshot_20210608-220500_JourneyPln.jpg" width="300" />
</p>


