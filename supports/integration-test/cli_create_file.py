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


def create_file_CLI(dir_num):
    """
    Please use this script in nodes with HDFS env.
    Each time create 10K * 50 files (10K in random dir) in HDFS_TEST_DIR.
    """
    for i in range(dir_num):
        file_index = 0
        dir_name = HDFS_TEST_DIR + random_string()
        # Create dir
        subprocess.call("hdfs dfs -mkdir " + dir_name, shell=True)
        command_arr = []
        for i in range(int(10000 / dir_num)):
            # run create file command in parallel
            command_arr.append("hdfs dfs -touchz " +
                               dir_name + "/" + str(file_index))
            file_index += 1
        exec_commands(command_arr)


if __name__ == '__main__':
    num = 50
    try:
        num = int(sys.argv[1])
    except ValueError:
        print("Usage: python cli_create_file [num]")
    except IndexError:
        pass
    create_file_CLI(num)
