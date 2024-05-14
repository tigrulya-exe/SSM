#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

import argparse
import unittest
from util import *

modify_actions1 = ['append', 'truncate']
modify_actions2 = ['concat', 'merge']


class TestMover(unittest.TestCase):

    def test_double_actions1(self):
        for action1 in modify_actions1:
            for action2 in MOVE_TYPES:
                file_path = create_random_file(FILE_SIZE)
                leng = random.randint(0, FILE_SIZE)
                cid = submit_cmdlet('{} -file {} -length {}; {} -file {}'.format(action1, file_path, leng, action2, file_path))
                cmd = wait_for_cmdlet(cid)
                self.assertTrue(cmd['state'] == "DONE", "Action chain: {} & {} failed.".format(action1, action2))

    def test_double_actions2(self):
        for action1 in modify_actions2:
            for action2 in MOVE_TYPES:
                src_path1 = create_random_file(FILE_SIZE)
                src_path2 = create_random_file(FILE_SIZE)
                dest_path = create_random_file(0)
                cid = submit_cmdlet('{} -file {},{} -dest {}; {} -file {}'.format(
                    action1, src_path1, src_path2, dest_path, action2, dest_path))
                cmd = wait_for_cmdlet(cid)
                self.assertTrue(cmd['state'] == "DONE", "Action chain: {} & {} failed.".format(action1, action2))

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('-size', default='64MB',
                        help="size of file, Default Value 64MB.")
    parser.add_argument('unittest_args', nargs='*')
    args, unknown_args = parser.parse_known_args()
    sys.argv[1:] = unknown_args
    print("The file size for test is {}.".format(args.size))
    FILE_SIZE = convert_to_byte(args.size)

    unittest.main()
