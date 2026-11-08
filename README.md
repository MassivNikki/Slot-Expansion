# Slot Expansion

Slot Expansion is a small incremental slot-machine game written in Java 21 with
Swing. Spin to earn coins and experience, then spend coins to improve each
machine and expand your collection.

## Running the game

From the repository root, build with Maven and launch the main class:

```shell
mvn package
java -cp target/classes slotexpansion.Main
```

The game loads its image assets and `SaveData` relative to the working
directory, so launch it from the repository root. Progress is written to
`SaveData` when the game window closes.

## How it works

- Each machine starts with a 3-by-3 symbol grid and five paylines. Matching
  symbols along a payline pay out coins; `x2` symbols multiply a line's payout.
- Three or more thunder symbols award free spins. Winning spins also award
  experience to that machine.
- Machine levels can award a pet. Pets multiply that machine's payouts, and
  higher-tier pets can also shorten its automatic-spin cooldown.
- Coin-funded upgrades can change symbol odds, add paylines, increase the grid
  size, adjust the minimum and maximum stake, and reduce spin cooldowns.
- New machines are available for purchase as the balance grows. Their upgrade
  costs scale with the machine's tier.

## Controls

Use **Spin** for a single spin or select **Auto** to repeat spins using the
displayed cooldown. Set the stake with the amount field, the up/down controls,
or the **min** and **max** buttons. **Upgrades** opens that machine's purchase
window; the grid-size upgrade is beside the grid.

The symbol and pet artwork is stored in `SlotImages/` and `pets/`.

## Project layout

```text
src/main/java/
└── slotexpansion/
    ├── Main.java                # Application entry point
    ├── app/                     # Startup, window lifecycle, and save/load coordination
    ├── machine/                 # Machine gameplay, grids, leveling, pets, and upgrades
    ├── player/                  # Shared player coin balance
    └── ui/                      # Main window and reusable Swing controls and styling
```

`Application` coordinates startup, loading, saving, and machine creation.
`SlotMachine` brings together the controls and game systems for one machine;
`SlotGrid` handles symbol generation, paylines, payouts, and free spins.
`UpgradeArea` and `UpgradeManager` present and apply machine upgrades.

The project currently declares no external Maven dependencies.
