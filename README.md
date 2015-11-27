# Lexic: The Android Word Game

Lexic is a word game that is available on the Android platform. Players are 
given 3 to 30 minutes to find as many words as possible on a grid of random letters.

Features:
 * 4x4 and 5x5 game boards
 * A dictionary of 77,000+ words
 * UK and US dictionaries

[![Available on F-Droid](https://f-droid.org/wiki/page/File:F-Droid-button_smaller.png)](https://f-droid.org/repository/browse/?fdfilter=lexic&fdid=net.healeys.lexic)

## Differences from original

This fork is based of the original from [http://code.google.com/p/lexic](http://code.google.com/p/lexic).

* Removed multiplayer options
 * Better support for screens with higher resolutions
 * Action bar support

That version still includes code to support online multiplayer games, even though
the server has been out of action for a year or so. In the interests of making
the user interface less confusing, this functionality has been removed from the code.

Other than these things, this fork is trying to stay true to the original as
much as possible.

## Contributing

### Reporting Issues

Please report any issues or suggest features on the [issue tracker](https://github.com/pserwylo/lexic/issues).

### Submitting changes

Pull requests will be warmly received at [https://github.com/pserwylo/lexic](https://github.com/pserwylo/lexic).

## Compiling

This app uses a typical gradle folder structure.
From the project directory, run `gradle assembleDebug` to build the package.

Alternatively, you can import the project into Android Studio and build from there.
