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

import sys
from util import *
from threading import Thread


def test_create_100M_0KB_thread(max_number):
    """ submit SSM action througth Restful API in parallel
    """
    cids = []
    dir_name = HDFS_TEST_DIR + random_string()
    for j in range(max_number):
        # each has 200K files
        cid = create_file(dir_name + "/" + str(j), 0)
        cids.append(cid)
    wait_for_cmdlets(cids)


if __name__ == '__main__':
    num = 20
    try:
        num = int(sys.argv[1])
    except ValueError:
        print("Usage: python dfsio_create_file [num]")
    except IndexError:
        pass
    max_number = 200000
    for i in range(num):
        t = Thread(target=test_create_100M_0KB_thread, args=(max_number,))
        t.start()
