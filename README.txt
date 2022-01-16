To change this mod's settings, go to data/config/settings.json

List of settings:

- TAS_acceptedAICoreIds: do not edit this for now. It only exists to prevent conflicts with other mods.

- TAS_hullSizeModifiers: edit this to change how much hull size impacts AP and monthly maintenance. The higher the number, the lower the AP impact and the higher the maintenance cost caused by an AI core.
Setting modifiers to 1.0 will cause them to be ignored in calculations.
Setting modifiers to less than 1.0 will drastically reduce monthly maintenance and increase AP impact. Not recommended.
Do not set any modifier to 0, it will crash the game. 

- TAS_maintenanceMultipliers: edit this to change the impact AI cores have on monthly maintenance. The higher the number, the more maintenance costs will increase.
Setting modifiers to 0 will cause them to be ignored in calculations, leaving maintenance to its base value.

- TAS_AICoreShipWeightReduction: edit this to change the impact AI cores have on Automated Points. Values must be between 0 and 1. The higher the value, the higher the impact.
Setting modifiers to 0 will cause them to be ignored in calculations, leaving AP unchanged.
Setting modifiers to 1 will case a 100% AP reduction, making the ship free.