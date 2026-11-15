package slotexpansion.machine;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Live state for one machine, independent of its Swing controls.
 * The slot grid uses the same state to calculate and display each spin.
 */
final class MachineState {
    private final SlotSymbolGenerator symbolGenerator;
    private BigInteger stake = BigInteger.ONE;
    private BigInteger minimumStake = BigInteger.valueOf(5);
    private BigInteger maximumStake = BigInteger.valueOf(20);
    private BigInteger xp = BigInteger.ZERO;
    private int level = 1;
    private Pet pet;
    private int freeSpins;
    private int columns = 3;
    private List<int[]> paylines = SlotPaylineLayouts.baseForColumnCount(3);
    private List<int[]> optionalPaylines = SlotPaylineLayouts.additionalForColumnCount(3);

    MachineState(SlotSymbolGenerator symbolGenerator) {
        this.symbolGenerator = symbolGenerator;
    }

    SlotSymbolGenerator symbols() { return symbolGenerator; }
    BigInteger stake() { return stake; }
    void setStake(BigInteger value) { stake = value; }
    BigInteger minimumStake() { return minimumStake; }
    void setMinimumStake(BigInteger value) { minimumStake = value; }
    BigInteger maximumStake() { return maximumStake; }
    void setMaximumStake(BigInteger value) { maximumStake = value; }
    BigInteger xp() { return xp; }
    void setXp(BigInteger value) { xp = value; }
    int level() { return level; }
    void setLevel(int value) { level = value; }
    Pet pet() { return pet; }
    void setPet(Pet value) { pet = value; }
    int freeSpins() { return freeSpins; }
    boolean hasFreeSpins() { return freeSpins > 0; }
    void consumeFreeSpin() { freeSpins--; }
    void awardFreeSpins(int amount) { freeSpins += amount; }
    int columns() { return columns; }
    List<int[]> paylines() { return List.copyOf(paylines); }
    int optionalPaylineCount() { return optionalPaylines.size(); }
    void addPayline(int index) { paylines.add(optionalPaylines.get(index)); }

    /** Expanding replaces paylines; chance upgrades are reset by the caller. */
    void setColumns(int value) {
        columns = value;
        paylines = new ArrayList<>(SlotPaylineLayouts.baseForColumnCount(value));
        optionalPaylines = SlotPaylineLayouts.additionalForColumnCount(value);
    }
}
