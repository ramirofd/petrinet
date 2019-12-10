# coding=utf8
# the above tag defines encoding for this document and is for Python 2.x compatibility

import re

regex = "T0(.*?)((T1(.*?)((T2(.*?)((T3(.*?)((T4(.*?))))|(T8(.*?)((T9(.*?)((T10(.*?)((T11(.*?)))))))))((T5(.*?)((T6(.*?)))))))))"

# test_str = "T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T3,T4,T3,T4,T3,T4,T3,T4,T3,T4,T3,T4,T3,T4,T3,T4,T3,T4,T3,T4,T5,T6,T5,T6,T5,T6,T5,T6,T5,T6,T5,T6,T5,T6,T5,T6,T5,T6,T5,T6,T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T8"
test_str = "T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T3,T4,T3,T4,T3,T4,T3,T4,T3,T4,T3,T4,T3,T4,T3,T4,T3,T4,T3,T4,T5,T6,T0,T1,T2,T8,T9,T10,T11,T5,T6,T5,T0,T1,T2,T3,T4,T6,T5,T0,T1,T2,T8,T9,T10,T11,T6,T5,T0,T1,T2,T3,T4,T6,T5,T0,T1,T2,T3,T4,T6,T5,T0,T1,T2,T3,T4,T6,T5,T0,T1"

prev_str = ''
while prev_str!=test_str:
    prev_str = test_str
    test_str = re.sub(regex, '\g<1>\g<4>\g<7>\g<10>\g<13>\g<15>\g<18>\g<21>\g<24>\g<27>\g<30>', test_str)

print(test_str)