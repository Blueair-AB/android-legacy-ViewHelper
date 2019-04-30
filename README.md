# ViewHelpers
Extension functions, utility functions and delegates for Views

[[![CircleCI](https://circleci.com/gh/flatcircle/ViewHelper.svg?style=svg)](https://circleci.com/gh/flatcircle/ViewHelper)

Installation
--------

```groovy
implementation 'io.flatcircle:viewhelper:{version}'
```


Usage
-----

| Function  | Description | Example |
| ------------- | ------------- | ------------- |
| .hideSoftKeyboard() | Hides the software keyboard if it is up | [Example](https://github.com/flatcircle/LiveDataHelper/blob/master/app/src/main/java/io/flatcircle/livedatahelperexample/MainActivity.kt#L34)  |
| .showSoftKeyboard() | Opens the software keyboard if it is down | [Example](https://github.com/flatcircle/LiveDataHelper/blob/master/app/src/main/java/io/flatcircle/livedatahelperexample/MainActivity.kt#L34) |
| .getPxFromDp() | Calculates pixels from dp input | [Example](https://github.com/flatcircle/LiveDataHelper/blob/master/app/src/main/java/io/flatcircle/livedatahelperexample/MainActivity.kt#L34) |
| .getDpfromPx() | Vice versa | [Example](https://github.com/flatcircle/LiveDataHelper/blob/master/app/src/main/java/io/flatcircle/livedatahelperexample/MainActivity.kt#L34) |
| .showShareDialog() | Opens a generic "share with" dialogue | [Example](https://github.com/flatcircle/LiveDataHelper/blob/master/app/src/main/java/io/flatcircle/livedatahelperexample/MainActivity.kt#L34) |
| .showEmailDialog() | Opens a send-email intent | [Example](https://github.com/flatcircle/LiveDataHelper/blob/master/app/src/main/java/io/flatcircle/livedatahelperexample/MainActivity.kt#L34) |
| .showConfirmation() | Shows a generic confirmation dialog | [Example](https://github.com/flatcircle/LiveDataHelper/blob/master/app/src/main/java/io/flatcircle/livedatahelperexample/MainActivity.kt#L34) |



| Extension  | Description | Example |
| ------------- | ------------- | ------------- |
| view.show() | Makes the View go GONE | [Example](https://github.com/flatcircle/LiveDataHelper/blob/master/app/src/main/java/io/flatcircle/livedatahelperexample/MainActivity.kt#L34)  |
| view.hide() | Makes the View go VISIBLE | [Example](https://github.com/flatcircle/LiveDataHelper/blob/master/app/src/main/java/io/flatcircle/livedatahelperexample/MainActivity.kt#L34)  |
