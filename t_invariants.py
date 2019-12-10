# coding=utf8
# the above tag defines encoding for this document and is for Python 2.x compatibility

import re
import numpy as np

regex = "T0(.*?)((T1(.*?)((T2(.*?)((T3(.*?)((T4(.*?))))|(T8(.*?)((T9(.*?)((T10(.*?)((T11(.*?)))))))))((T5(.*?)((T6(.*?)))))))))"

t_order = ['T0', 'T1', 'T10', 'T11', 'T2', 'T3', 'T4', 'T5', 'T6', 'T8', 'T9']
m0 = [0,    0,    1,    2,    1,    0,    0,    0,    0,    0,    1,    0,   10,    0,   30,   30]
mF = [0,    0,    0,    2,    1,    9,    0,    0,    0,    1,    1,    0,    0,    0,   30,   30]
inc = [[0,     0,     1,    -1,     0,     0,     0,     0,     0,     0,     0, ],
       [0,     0,     0,     0,     1,    -1,     0,     0,     0,    -1,     0, ],
       [0,     0,     0,     0,     0,     0,     0,    -1,     1,     0,     0, ],
       [-1,     1,     0,     0,     0,     0,     0,     0,     0,     0,     0, ],
       [0,    -1,     0,     0,     1,     0,     0,     0,     0,     0,     0, ],
       [0,     0,     0,     1,     0,     0,     1,    -1,     0,     0,     0, ],
       [1,    -1,     0,     0,     0,     0,     0,     0,     0,     0,     0, ],
       [0,     0,     0,     0,     0,     1,    -1,     0,     0,     0,     0, ],
       [0,     0,    -1,     0,     0,     0,     0,     0,     0,     0,     1, ],
       [0,     0,     0,     0,     0,     0,     0,     1,    -1,     0,     0, ],
       [0,     0,    -1,     1,     0,     0,     0,     0,     0,    -1,     1, ],
       [0,     1,     0,     0,    -1,     0,     0,     0,     0,     0,     0, ],
       [-1,     0,     0,     0,     0,     0,     0,     0,     1,     0,     0, ],
       [0,     0,     0,     0,     0,     0,     0,     0,     0,     1,    -1, ],
       [0,     0,     0,     0,     0,    -1,     1,     0,     0,     0,     0, ],
       [0,     0,     1,     0,     0,     0,     0,     0,     0,    -1,     0, ]]
# test_str = "T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T3,T4,T3,T4," \
#            "T3,T4,T3,T4,T3,T4,T3,T4,T3,T4,T3,T4,T3,T4,T3,T4,T5,T6,T5,T6,T5,T6,T5,T6,T5,T6,T5,T6,T5,T6,T5,T6,T5,T6," \
#            "T5,T6,T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T8"
test_str = "T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T0,T1,T2,T8,T9,T10,T11," \
           "T8,T9,T10,T11,T8,T9,T10,T11,T8,T9,T10,T11,T8,T9,T10,T11,T8,T9,T10,T11,T8,T9,T10,T11,T8,T9,T10,T11,T8,T9," \
           "T10,T11,T8,T9,T10,T11,T5,T6,T0,T1,T2,T8,T9,T10,T11,T5,T6,T5,T0,T1,T2,T3,T4,T6,T5,T0,T1,T2,T3,T4,T6,T5,T0," \
           "T1,T2,T3,T4,"

prev_str = ''
while prev_str!=test_str:
    prev_str = test_str
    test_str = re.sub(regex, '\g<1>\g<4>\g<7>\g<10>\g<13>\g<15>\g<18>\g<21>\g<24>\g<27>\g<30>', test_str)

unique_words = set(test_str.split(','))
unique_words.remove('')
fire = np.zeros(len(t_order))
for word in unique_words:
    fire[t_order.index(word)] = test_str.count(word+',')

mCalc = (np.asarray(m0) + np.asarray(inc).dot(fire)).astype(int)
print(np.asarray(mF))
print(mCalc)
print(test_str)
