// import java.util.*;

// public class E_1_Prime_Gaming_Easy_Version {
//     static final int MOD = 1_000_000_007;

//     public static void main(String[] args) {
//         Scanner sc = new Scanner(System.in);
//         int t = sc.nextInt();
//         while (t-- > 0) {
//             int n = sc.nextInt();
//             int m = sc.nextInt();
//             int k = sc.nextInt();
//             boolean[] originalGoodIndices = new boolean[n + 1];
//             for (int i = 0; i < k; i++) {
//                 originalGoodIndices[sc.nextInt()] = true;
//             }

//             if (n == 1) {
//                 long ans = (long) m * (m + 1) / 2;
//                 System.out.println(ans % MOD);
//                 continue;
//             }

//             // Case m = 1: All piles have 1 stone. Final pile is always 1.
//             if (m == 1) {
//                 System.out.println(1);
//                 continue;
//             }

//             // Case m = 2: Piles can have 1 or 2 stones.
//             // Iterate over all 2^n configurations.
//             // For each configuration, we need to simulate the game to find the final x.
//             long totalSum = 0;

//             // This is a minimax problem.
//             // The state can be defined by (remaining_piles_list, turn_number).
//             // Since N is small (up to 20), we can use memoization for the minimax part,
//             // or if N is very small, even direct recursion.
//             // However, the number of good indices can be N.

//             // The interpretation of "good index" is crucial.
//             // "Choose any integer i such that 1<=i<=p (where p is the number of piles left) and i is good"
//             // "c1,c2,…,ck (1=c1<c2<…<ck≤n) — the good indices."
//             // "It is guaranteed that 1 is always a good index (i.e. c1=1)."
//             // This means the 'good' property applies to the *current* 1-based index in the *remaining* sequence of piles.
//             // So if you have piles [A, B, C, D] and c1=1, c2=3 are good.
//             // Alice can pick pile A (index 1) or pile C (index 3).
//             // If she picks A, remaining are [B, C, D].
//             // Now Bob plays. The *new* index 1 is B, *new* index 2 is C, *new* index 3 is D.
//             // So Bob can pick pile B (new index 1) or pile D (new index 3).
//             // This is equivalent to saying the set of good positions {c1, ..., ck} applies to the current array of piles.

//             // To handle this, we can pass a bitmask representing the available good positions in the current array.
//             // The state for our memoization would be (bitmask_of_remaining_piles, player_turn).
//             // `dp` will store the outcome of the game.
//             // `dp[mask]` will store the best outcome (final pile value) for the player whose turn it is,
//             // given the piles represented by the mask.
//             // The mask represents the original indices of piles that are still in play.
//             // So, if the original array was `piles_orig`, and mask = `01011` (binary),
//             // it means `piles_orig[0], piles_orig[2], piles_orig[4]` are still in play.
//             // When we consider removing a pile, we need to map its *current* 1-based index to its original index.
//             // Then we check if this current 1-based index is in `originalGoodIndices`. This sounds like the way.

//             for (int i = 0; i < (1 << n); i++) { // Iterate through all 2^n configurations
//                 int[] piles = new int[n];
//                 for (int j = 0; j < n; j++) {
//                     piles[j] = ((i >> j) & 1) + 1; // 1 or 2 stones
//                 }

//                 // Map to store memoized results for (current_mask_of_piles_left, player_turn)
//                 // We'll use a single map for outcomes, since it's a value.
//                 // It implicitly encodes the player: if Alice, she maximizes this value. If Bob, he minimizes.
//                 // The value returned from solve is the final pile value.
//                 Map<Integer, Integer> memo = new HashMap<>();
//                 totalSum = (totalSum + solve(piles, originalGoodIndices, (1 << n) - 1, 0, n, memo)) % MOD;
//             }

//             System.out.println(totalSum);
//         }
//         sc.close();
//     }

//     // `piles`: the original configuration of stones
//     // `originalGoodIndices`: boolean array where originalGoodIndices[j] is true if index j is good
//     // `currentPilesMask`: bitmask representing which *original* piles are still in play (1 = in play, 0 = removed)
//     // `player`: 0 for Alice (maximizer), 1 for Bob (minimizer)
//     // `n`: original number of piles
//     // `memo`: memoization table for (mask, player) -> result
//     private static int solve(int[] piles, boolean[] originalGoodIndices, int currentPilesMask, int player, int n, Map<Integer, Integer> memo) {
//         // Create a unique key for memoization.
//         // We need to combine currentPilesMask and player.
//         // N is up to 20, so mask is max 2^20-1. We can put player in the highest bit.
//         int memoKey = (currentPilesMask << 1) | player;
//         if (memo.containsKey(memoKey)) {
//             return memo.get(memoKey);
//         }

//         // Count how many piles are currently left
//         int numPilesLeft = Integer.bitCount(currentPilesMask);

//         // Base case: Only one pile left, game ends.
//         if (numPilesLeft == 1) {
//             // Find the value of the remaining pile
//             for (int i = 0; i < n; i++) {
//                 if (((currentPilesMask >> i) & 1) == 1) {
//                     memo.put(memoKey, piles[i]);
//                     return piles[i];
//                 }
//             }
//         }

//         int bestOutcome;
//         if (player == 0) { // Alice's turn (Maximizer)
//             bestOutcome = -1; // Since values are 1 or 2, -1 is smaller than any possible outcome
//             for (int i = 0; i < n; i++) { // Iterate through original pile indices
//                 if (((currentPilesMask >> i) & 1) == 1) { // If original pile 'i' is still in play
//                     // Find its *current* 1-based index in the remaining piles
//                     int current1BasedIndex = 0;
//                     for (int j = 0; j <= i; j++) {
//                         if (((currentPilesMask >> j) & 1) == 1) {
//                             current1BasedIndex++;
//                         }
//                     }

//                     // Check if this current 1-based index is a good index
//                     if (originalGoodIndices[current1BasedIndex]) {
//                         // If Alice removes this pile, calculate the new mask
//                         int newPilesMask = currentPilesMask ^ (1 << i);
//                         int outcome = solve(piles, originalGoodIndices, newPilesMask, 1, n, memo); // Bob plays next
//                         bestOutcome = Math.max(bestOutcome, outcome);
//                     }
//                 }
//             }
//         } else { // Bob's turn (Minimizer)
//             bestOutcome = 3; // Since values are 1 or 2, 3 is larger than any possible outcome
//             for (int i = 0; i < n; i++) { // Iterate through original pile indices
//                 if (((currentPilesMask >> i) & 1) == 1) { // If original pile 'i' is still in play
//                     // Find its *current* 1-based index in the remaining piles
//                     int current1BasedIndex = 0;
//                     for (int j = 0; j <= i; j++) {
//                         if (((currentPilesMask >> j) & 1) == 1) {
//                             current1BasedIndex++;
//                         }
//                     }

//                     // Check if this current 1-based index is a good index
//                     if (originalGoodIndices[current1BasedIndex]) {
//                         // If Bob removes this pile, calculate the new mask
//                         int newPilesMask = currentPilesMask ^ (1 << i);
//                         int outcome = solve(piles, originalGoodIndices, newPilesMask, 0, n, memo); // Alice plays next
//                         bestOutcome = Math.min(bestOutcome, outcome);
//                     }
//                 }
//             }
//         }

//         memo.put(memoKey, bestOutcome);
//         return bestOutcome;
//     }
// }


import java.util.*;

public class E_1_Prime_Gaming_Easy_Version {
    static final int MOD = 1_000_000_007;

    // Memoization table: Map<memoKey, result_final_pile_value>
    // memoKey combines currentPilesMask and player (0 for Alice, 1 for Bob)
    private static Map<Integer, Integer> memo; 
    private static int[] currentPiles; // The actual stone counts for the current configuration
    private static boolean[] originalGoodIndicesGlobal; // Which *original* 1-based indices are good
    private static int N_global; // Total number of piles

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int t = sc.nextInt();
        while (t-- > 0) {
            int n = sc.nextInt();
            int m = sc.nextInt();
            int k = sc.nextInt();
            
            originalGoodIndicesGlobal = new boolean[n + 1];
            for (int i = 0; i < k; i++) {
                originalGoodIndicesGlobal[sc.nextInt()] = true;
            }
            N_global = n;

            if (n == 1) {
                long ans = (long) m * (m + 1) / 2;
                System.out.println(ans % MOD);
                continue;
            }

            if (m == 1) {
                System.out.println(1);
                continue;
            }

            long totalSum = 0;

            // Iterate over all 2^n configurations for m=2
            for (int i = 0; i < (1 << n); i++) {
                currentPiles = new int[n];
                for (int j = 0; j < n; j++) {
                    currentPiles[j] = ((i >> j) & 1) + 1; // 1 or 2 stones
                }

                memo = new HashMap<>(); // Reset memoization for each configuration
                totalSum = (totalSum + solve((1 << n) - 1, 0)) % MOD;
            }

            System.out.println(totalSum);
        }
        sc.close();
    }

    /**
     * Solves the game for a given state.
     * @param currentPilesMask Bitmask representing which original piles are still in play.
     * @param player 0 for Alice (maximizer), 1 for Bob (minimizer).
     * @return The value of the final pile if both play optimally.
     */
    private static int solve(int currentPilesMask, int player) {
        // Create a unique key for memoization (mask << 1 | player)
        int memoKey = (currentPilesMask << 1) | player;
        if (memo.containsKey(memoKey)) {
            return memo.get(memoKey);
        }

        int numPilesLeft = Integer.bitCount(currentPilesMask);

        // Base case: Only one pile left.
        if (numPilesLeft == 1) {
            for (int i = 0; i < N_global; i++) {
                if (((currentPilesMask >> i) & 1) == 1) { // Find the remaining pile
                    memo.put(memoKey, currentPiles[i]);
                    return currentPiles[i];
                }
            }
        }

        int bestOutcome;
        if (player == 0) { // Alice's turn (Maximizer)
            bestOutcome = -1; // Smallest possible outcome is 1, so -1 is safe.
            for (int i = 0; i < N_global; i++) { // Iterate through original pile indices
                if (((currentPilesMask >> i) & 1) == 1) { // If original pile 'i' is still in play
                    
                    // Calculate its *current* 1-based index in the sequence of remaining piles.
                    // This is (number of set bits before 'i' in currentPilesMask) + 1.
                    int current1BasedIndex = Integer.bitCount(currentPilesMask & ((1 << i) - 1)) + 1;
                    
                    if (originalGoodIndicesGlobal[current1BasedIndex]) {
                        // Simulate removing this pile
                        int newPilesMask = currentPilesMask ^ (1 << i);
                        int outcome = solve(newPilesMask, 1); // Bob plays next
                        bestOutcome = Math.max(bestOutcome, outcome);
                    }
                }
            }
        } else { // Bob's turn (Minimizer)
            bestOutcome = 3; // Largest possible outcome is 2, so 3 is safe.
            for (int i = 0; i < N_global; i++) { // Iterate through original pile indices
                if (((currentPilesMask >> i) & 1) == 1) { // If original pile 'i' is still in play
                    
                    // Calculate its *current* 1-based index
                    int current1BasedIndex = Integer.bitCount(currentPilesMask & ((1 << i) - 1)) + 1;
                    
                    if (originalGoodIndicesGlobal[current1BasedIndex]) {
                        // Simulate removing this pile
                        int newPilesMask = currentPilesMask ^ (1 << i);
                        int outcome = solve(newPilesMask, 0); // Alice plays next
                        bestOutcome = Math.min(bestOutcome, outcome);
                    }
                }
            }
        }

        memo.put(memoKey, bestOutcome);
        return bestOutcome;
    }
}