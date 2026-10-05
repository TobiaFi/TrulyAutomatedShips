To tailor this mod to your own preferences, edit the setting present in data/config/settings.json

List of settings:

- TAS_acceptedAICoreIds: do not edit this for now. It only exists to prevent conflicts with other mods.

- TAS_hullSizeModifiers: edit this to change how much hull size impacts AP and Peak Performance Time. The higher the number, the lower the AP impact and PPT caused by an AI core.
Setting modifiers to 1.0 will cause them to be ignored in calculations.
Setting modifiers to less than 1.0 will drastically reduce the PPT downsides and increase AP impact. Not recommended.
Do not set any modifier to 0, it will crash the game.

- TAS_maintenanceMultipliers: edit this to change the impact AI cores have on CR/PPT. The higher the number, the more CR consumption will increase and PPT will decrease.
Setting modifiers to 0 will cause them to be ignored in calculations, leaving maintenance to its base value.

- TAS_AICoreShipWeightReduction: edit this to change the impact AI cores have on Automated Points. Values must be between 0 and 1. The higher the value, the higher the impact.
Setting modifiers to 0 will cause them to be ignored in calculations, leaving AP unchanged.
Setting modifiers to 1 will case a 100% AP reduction, making the ship free.
