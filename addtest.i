//iloc add test
// add 2 + 3 = 5
// add 3 + 3 = 6
// add 5 + 6 = 11
//add 5 + 11 = 16
//add 16 + 2 = 18

loadI 2 => r1
loadI 3 => r2
add r1, r2 => r3
add r2, r2 => r4
add r3, r4 => r5
add r3, r5 => r6
add r6, r1 => r7
loadI 2048 => r8
store r7 => r8
output 2048
