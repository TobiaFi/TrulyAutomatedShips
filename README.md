# Truly Automated Ships v1.2

This mod intends to change the way AI officers assigned to automated ships affect the rest of your fleet.

In the vanilla game, AI officers increase the "automated ship points" of the ship they're attached to, and to me it doesn't make sense. If your automated fleet relies on the computing power of the rest of the fleet to coordinate itself - and that's why having too many automated ships leads to a decrease in maximum efficiency for all of them - then grabbing a tonne of GPUs and stitching them together with a ship should actually lift some of the weight from your regular ships, not add even more!

With this mod, when you slap a supercomputer on a hull, it will actually make the ship more autonomous, and decrease its "automated ship points," leaving you with the ability to increase the size of your automated fleet even more!
But beware: all of this added computing power will require constant maintenance and a large energy supply. After all, you wouldn't expect a giant AI core with the capacity to manage entire planets to be easy to sustain, would you? The good news is that small ships will be reasonably cheap, but as hull size increases, so do maintenance costs.

## Let's talk numbers

I created this mod with the intent of making it more viable to have powerful automated fleets. It's possible, for example, to have up to three Radiants in your fleet without suffering any CR maluses. The drawback is an increase in the amount monthly supplies required to keep your ships operational. You'll really have to think about how much increasing the power of a given ship is worth to you.

This mod will apply a reduction to the "automated ship points" of any ship outfitted with an AI core. The reduction will depend on the type of core:

- Alpha core base reduction: -75%
- Beta core base reduction: -50%
- Gamma core base reduction: -25%

Having an AI core as an officer will also cause a hefty increase to its monthly maintenance supply cost:

- Alpha core base increase: +100%
- Beta Core base increase: +75%
- Gamma Core base increase: +50%

A modifier will be applied to these base values depending on hull size. Automated points base reduction will be divided by these modifiers (making AI cores less effective on larger ships), and base maintenance cost will be multiplied by the modifiers (reflecting the vast amount of resources required to manage larger ships):

- Frigates: 1
- Destroyers: 1.25
- Cruisers: 1.5
- Capitals: 2

## Settings customisation

Thanks to Bisou Gai's amazing contributions, TAS now has a "settings" file that can be edited to change how the mod operates!
To do so, just follow the instructions in README.txt and you'll be able to set up your own configuration, change any of the previously stated percentages and modifiers, and even disable them altogether.

## v1.2 changelog

- Updated values and calculations to adapt to the new patch
- Added a modifier to AP reduction based on hull size (sorry, running 12 Radiants per fleet would've been a bit too much)
- Added settings file for customisation

## v1.1.2 changelog

- Fixed issue that caused cores to change the automated points of multiple ships at once

## v1.1.1 changelog

- Fixed compatibility issue with Seeker

## v1.1 changelog

- Changed base maintenance increase from 300/200/100 to 150/100/50
- Added hull size multiplier
- Added tooltip on the "Automated Ship" hullmod to explain the increase in cost
- Changed "Automated Ships" skill description accordingly

## [Download here](https://github.com/TobiaFi/TrulyAutomatedShips/releases/tag/v1.2)

For any questions or suggestions, please contact the author of this mod.

Fractal Softworks Forums: [TobiaF](https://fractalsoftworks.com/forum/index.php?action=profile;u=15979)

Discord: Toby#2057

Bisou Gai's info can be found on the "contributors" section for the mod on Github (ThibautSF).
