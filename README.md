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
`SaveData` when the game window closes. A failed save leaves the window open
and reports the error rather than discarding progress.

Run the automated checks with `mvn test`. The machine integration tests create
Swing windows and therefore require a graphical desktop (or a virtual display).

## How it works

- Each machine starts with a 3-by-3 symbol grid and five paylines. Matching
  symbols along a payline pay out coins; `x2` symbols multiply a line's payout.
- Three or more thunder symbols award free spins. Winning spins also award
  experience to that machine.
- During free spins, that machine's stake and upgrade controls are locked.
  Other machines can still be used. The x2 bonus is temporary.
- Unlocked sevens have a 3% base chance in every column. The x2 symbol only appears in
  interior columns; on edge columns its probability is assigned to tier-one
  symbols instead.
- Tier-two, tier-three, tier-four, and seven symbols are purchased separately
  for each machine, in any order. Until unlocked, their rolls become tier-one
  symbols. Unlock prices scale with the machine's tier.
- Machine levels can award a pet. Pets multiply that machine's payouts, and
  higher-tier pets can also shorten its automatic-spin cooldown.
- Coin-funded upgrades can change symbol odds, add paylines, increase the grid
  size, adjust the minimum and maximum stake, and reduce spin cooldowns.
- New machines are available for purchase as the balance grows. Their upgrade
  costs scale with the machine's tier.

## Controls

Use **Spin** for a single spin or select **Auto** to repeat spins using the
displayed cooldown. Set the stake with the amount field, the up/down controls,
or the **min** and **max** buttons. Each machine's upgrade panel sits below its
grid: **Increase** improves chances, while **Unlock** buys symbol tiers and
paylines. The grid-size upgrade is beside the grid. The window grows with the
machines and can extend beyond the display; position it as needed. On Windows,
the native window height limit can clip the layout, so the machine positions
may need adjustment.

Amounts use four digits, truncating extra digits without changing the exact
value: for example, `1.000t`, `12.34M`, and `100.0T`. After `Dc`, suffixes
continue from `aa` through `zz`, then switch to scientific notation. The stake
field accepts these suffixes or scientific notation as well as whole numbers.
An unchanged abbreviated stake keeps its exact stored value.

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

### Where to make changes

| Change | Start here | Related effects |
| --- | --- | --- |
| Symbol odds or special symbols | `SymbolChanceConfig` and `SlotSymbolGenerator` | `UpgradeManager` changes the same chance bands; `UpgradeArea` displays them. |
| Paylines | `SlotPaylineLayouts` | Each row-major mask must select one cell per column. `PaylineEvaluator` evaluates matching prefixes. |
| Payouts or free-spin awards | `PaylineEvaluator` or `SpinCalculator` | `SlotGrid` applies the result to the balance and displays its winning lines. |
| A machine upgrade | `UpgradeArea` and `UpgradeManager` | Define its cost and control, apply its effect, and include persistent state in `MachineSave` if needed. Symbol unlocks are stored in `MachineState`. |
| Prices or milestones | `UpgradePrice` | `UpgradePurchase` applies the cost; saved purchase counts rebuild prices on load. |
| Levels or pets | `LevelProgression`, `SlotLevelComponent`, and `Pet` | Winning payouts award XP; `SlotMachine` assigns pets and refreshes the auto-spin timer. |
| Save data | `MachineSave` and `SaveRepository` | `SlotMachine.snapshot` and `restore` translate between saved records and the live machine. |
| Appearance or controls | `SlotMachine`, `SlotGrid`, and `ui` | Game calculations live in the machine classes above, not the custom Swing painters. |

`Main` starts Swing on its event-dispatch thread. `Application` loads the
shared `CoinBalance` and creates each machine. `MachineState` owns each
machine's stake, level, XP, pet, chances, paylines, and free-spin count; Swing
components display or update that state. For a spin, `SlotGrid` requests a
`SpinCalculator` result, applies the stake and payout, awards free spins, and
updates the display. The payout uses the paytable, x2 symbols, pet multiplier,
and `max(1, level / 2)` with integer division. The minimum stake remains free.

Regular upgrade prices start at 15% growth per purchase; the growth rate
increases by five percentage points on each subsequent purchase, up to 50%.
The second machine starts at 150,000 coins, and the first grid expansion at 75,000 coins
times the machine tier squared. Later milestones of each kind cost six times
as much. The first machine milestone is tuned for roughly 10–20 minutes at
about one spin per second, but luck and upgrade choices can change that.

Only winning spins grant XP, equal to their payout, so larger stakes can earn
more XP when they win. Level requirements are `500 × level³ × 1.1^(level − 1)`,
rounded down to whole XP. Larger payouts and pets still help progression, but
the rising requirement limits runaway leveling.

Each chance upgrade takes probability from the lowest available lower tier.
When no lower tier remains, that upgrade is disabled. A grid expansion resets
chance upgrades, extra paylines, their purchase prices, and the pet.
It also locks the symbol tiers again. Chance upgrades for a tier are available
only after that tier is unlocked.

`SaveRepository` reads old 17- and 22-field records as well as current 23-field
records. The extra fields retain purchase counts even when an upgrade's chance
has been reduced by another upgrade. Older saves did not record that history,
so their price counts are estimated from remaining upgrades. Records without
symbol unlock flags start with tiers locked but retain their chance upgrades.
Saves are not migrated between balancing curves; start a fresh game after
balancing changes. Writes use a
temporary file and an atomic replacement. Active free spins and animation are
not saved; the permanent upgrade values are.

JUnit is used only for tests; the game itself has no external dependencies.
