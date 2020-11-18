package de.uol.snakeinc.possibleMoves;

import java.util.BitSet;
import java.util.HashSet;

public interface IntSet {

    boolean isEmpty();

    boolean contains(int Int);

    void add(int Int);

    void remove(int Int);

    void clear();

    /**
     * creates a new IntSet that can at least hold that integers up to size.
     * @param size the minimal capacity of the new IntSet
     * @return a new IntSet
     */
    static IntSet ofSize(int size) {
        if (size < 32) {
            return new SmallIntegerSet();
        } else {
            return new LargeIntegerSet();
        }
    }

    /**
     * creates a copy of of.
     * @param of the original
     * @return a copy of the original
     */
    static IntSet of(IntSet of) {
        if (of instanceof SmallIntegerSet) {
            var res = new SmallIntegerSet();
            res.set = ((SmallIntegerSet) of).set;
            return res;
        } else if (of instanceof LargeIntegerSet) {
            var res = new LargeIntegerSet();
            res.set = new HashSet<>(((LargeIntegerSet) of).set);
            return res;
        } else {
            throw new IllegalStateException("wrong IntSet");
        }
    }

    class SmallIntegerSet implements IntSet {

        private SmallIntegerSet() {

        }

        private int set = 0;

        private enum numbers {
            _0(1),
            _1(2),
            _2(4),
            _3(8),
            _4(16),
            _5(32),
            _6(64),
            _7(128),
            _8(256),
            _9(512),
            _10(1024),
            _11(2048),
            _12(4096),
            _13(8192),
            _14(16341),
            _15(32768),
            _16(_15.number * 2),
            _17(_16.number * 2),
            _18(_17.number * 2),
            _19(_18.number * 2),
            _20(_19.number * 2),
            _21(_20.number * 2),
            _22(_21.number * 2),
            _23(_22.number * 2),
            _24(_23.number * 2),
            _25(_24.number * 2),
            _26(_25.number * 2),
            _27(_26.number * 2),
            _28(_27.number * 2),
            _29(_28.number * 2),
            _30(_29.number * 2),
            _31(Integer.MIN_VALUE);

            private numbers(int number) {
                this.number = number;
            }

            int number;
        }

        @Override
        public void clear() {
            set = 0;
        }

        public boolean isEmpty() {
            return set == 0;
        }

        public boolean contains(int Int) {
            switch (Int) {
                case 0:
                    return (set & numbers._0.number) > 0;
                case 1:
                    return (set & numbers._1.number) > 0;
                case 2:
                    return (set & numbers._2.number) > 0;
                case 3:
                    return (set & numbers._3.number) > 0;
                case 4:
                    return (set & numbers._4.number) > 0;
                case 5:
                    return (set & numbers._5.number) > 0;
                case 6:
                    return (set & numbers._6.number) > 0;
                case 7:
                    return (set & numbers._7.number) > 0;
                case 8:
                    return (set & numbers._8.number) > 0;
                case 9:
                    return (set & numbers._9.number) > 0;
                case 10:
                    return (set & numbers._10.number) > 0;
                case 11:
                    return (set & numbers._11.number) > 0;
                case 12:
                    return (set & numbers._12.number) > 0;
                case 13:
                    return (set & numbers._13.number) > 0;
                case 14:
                    return (set & numbers._14.number) > 0;
                case 15:
                    return (set & numbers._15.number) > 0;
                case 16:
                    return (set & numbers._16.number) > 0;
                case 17:
                    return (set & numbers._17.number) > 0;
                case 18:
                    return (set & numbers._18.number) > 0;
                case 19:
                    return (set & numbers._19.number) > 0;
                case 20:
                    return (set & numbers._20.number) > 0;
                case 21:
                    return (set & numbers._21.number) > 0;
                case 22:
                    return (set & numbers._22.number) > 0;
                case 23:
                    return (set & numbers._23.number) > 0;
                case 24:
                    return (set & numbers._24.number) > 0;
                case 25:
                    return (set & numbers._25.number) > 0;
                case 26:
                    return (set & numbers._26.number) > 0;
                case 27:
                    return (set & numbers._27.number) > 0;
                case 28:
                    return (set & numbers._28.number) > 0;
                case 29:
                    return (set & numbers._29.number) > 0;
                case 30:
                    return (set & numbers._30.number) > 0;
                case 31:
                    return (set & numbers._31.number) < 0; // watch it! less than zero
                default:
                    return false;
            }
        }

        public void add(int Int) {
            switch (Int) {
                case 0:
                    set |= numbers._0.number;
                    return;
                case 1:
                    set |= numbers._1.number;
                    return;
                case 2:
                    set |= numbers._2.number;
                    return;
                case 3:
                    set |= numbers._3.number;
                    return;
                case 4:
                    set |= numbers._4.number;
                    return;
                case 5:
                    set |= numbers._5.number;
                    return;
                case 6:
                    set |= numbers._6.number;
                    return;
                case 7:
                    set |= numbers._7.number;
                    return;
                case 8:
                    set |= numbers._8.number;
                    return;
                case 9:
                    set |= numbers._9.number;
                    return;
                case 10:
                    set |= numbers._10.number;
                    return;
                case 11:
                    set |= numbers._11.number;
                    return;
                case 12:
                    set |= numbers._12.number;
                    return;
                case 13:
                    set |= numbers._13.number;
                    return;
                case 14:
                    set |= numbers._14.number;
                    return;
                case 15:
                    set |= numbers._15.number;
                    return;
                case 16:
                    set |= numbers._16.number;
                    return;
                case 17:
                    set |= numbers._17.number;
                    return;
                case 18:
                    set |= numbers._18.number;
                    return;
                case 19:
                    set |= numbers._19.number;
                    return;
                case 20:
                    set |= numbers._20.number;
                    return;
                case 21:
                    set |= numbers._21.number;
                    return;
                case 22:
                    set |= numbers._22.number;
                    return;
                case 23:
                    set |= numbers._23.number;
                    return;
                case 24:
                    set |= numbers._24.number;
                    return;
                case 25:
                    set |= numbers._25.number;
                    return;
                case 26:
                    set |= numbers._26.number;
                    return;
                case 27:
                    set |= numbers._27.number;
                    return;
                case 28:
                    set |= numbers._28.number;
                    return;
                case 29:
                    set |= numbers._29.number;
                    return;
                case 30:
                    set |= numbers._30.number;
                    return;
                case 31:
                    set |= numbers._31.number;
                    return;
                default:
            }
        }

        public void remove(int Int) {
            switch (Int) {
                case 0:
                    set ^= numbers._0.number;
                    break;
                case 1:
                    set ^= numbers._1.number;
                    break;
                case 2:
                    set ^= numbers._2.number;
                    break;
                case 3:
                    set ^= numbers._3.number;
                    break;
                case 4:
                    set ^= numbers._4.number;
                    break;
                case 5:
                    set ^= numbers._5.number;
                    break;
                case 6:
                    set ^= numbers._6.number;
                    break;
                case 7:
                    set ^= numbers._7.number;
                    break;
                case 8:
                    set ^= numbers._8.number;
                    break;
                case 9:
                    set ^= numbers._9.number;
                    break;
                case 10:
                    set ^= numbers._10.number;
                    break;
                case 11:
                    set ^= numbers._11.number;
                    break;
                case 12:
                    set ^= numbers._12.number;
                    break;
                case 13:
                    set ^= numbers._13.number;
                    break;
                case 14:
                    set ^= numbers._14.number;
                    break;
                case 15:
                    set ^= numbers._15.number;
                    break;
                case 16:
                    set ^= numbers._16.number;
                    break;
                case 17:
                    set ^= numbers._17.number;
                    break;
                case 18:
                    set ^= numbers._18.number;
                    break;
                case 19:
                    set ^= numbers._19.number;
                    break;
                case 20:
                    set ^= numbers._20.number;
                    break;
                case 21:
                    set ^= numbers._21.number;
                    break;
                case 22:
                    set ^= numbers._22.number;
                    break;
                case 23:
                    set ^= numbers._23.number;
                    break;
                case 24:
                    set ^= numbers._24.number;
                    break;
                case 25:
                    set ^= numbers._25.number;
                    break;
                case 26:
                    set ^= numbers._26.number;
                    break;
                case 27:
                    set ^= numbers._27.number;
                    break;
                case 28:
                    set ^= numbers._28.number;
                    break;
                case 29:
                    set ^= numbers._29.number;
                    break;
                case 30:
                    set ^= numbers._30.number;
                    break;
                case 31:
                    set ^= numbers._31.number;
                    break;
                default:
            }
        }
    }

    class LargeIntegerSet implements IntSet {

        private HashSet<Integer> set = new HashSet<>();

        public LargeIntegerSet() {
        }

        @Override
        public void clear() {
            set.clear();
        }

        @Override
        public boolean isEmpty() {
            return set.isEmpty();
        }

        @Override
        public boolean contains(int Int) {
            return set.contains(Int);
        }

        @Override
        public void add(int Int) {
            set.add(Int);
        }

        @Override
        public void remove(int Int) {
            remove(Int);
        }
    }

    class IntBitSet implements IntSet {

        private BitSet set;

        public IntBitSet(int size) {
            set = new BitSet(size);
            set.clear();
        }

        @Override
        public void clear() {
            set.clear();
        }

        @Override
        public boolean isEmpty() {
            return set.isEmpty();
        }

        @Override
        public boolean contains(int Int) {
            return set.get(Int);
        }

        @Override
        public void add(int Int) {
            set.set(Int);
        }

        @Override
        public void remove(int Int) {
            set.clear(Int);
        }
    }
}
