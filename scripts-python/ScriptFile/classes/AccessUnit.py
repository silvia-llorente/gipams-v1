from classes.AU_classes.AccessUnitHeader import AccessUnitHeader
from classes.AU_classes.AccessUnitInformation import AccessUnitInformation
from classes.AU_classes.AccessUnitProtection import AccessUnitProtection
from classes.Block import Block
import os

class AccessUnit:
    def __init__(self):
        self.AUHeader = None
        self.AU_information = None
        self.AU_protection = None
        self.blocks = []

    def addHeader(self,access_unit_ID,parameter_set_ID,AU_type,reads_count):
        self.AUHeader = AccessUnitHeader(access_unit_ID,parameter_set_ID,AU_type,reads_count)

    def addInformation(self,dataset_group_ID,dataset_ID,AU_information_value):
        self.AU_information = AccessUnitInformation(dataset_group_ID,dataset_ID,AU_information_value)
        
    def addProtection(self,dataset_group_ID,dataset_ID,AU_protection_value):
        self.AU_protection = AccessUnitProtection(dataset_group_ID,dataset_ID,AU_protection_value)

    def addBlock(self,payload):
        self.blocks.append(Block(len(self.blocks),payload))
        self.AUHeader.num_blocks += 1
    
    def write(self,path):
        for item in self.__dict__.items():
            if not item[0][0].islower() and item[1] != None:
                item[1].write(path)

        i = 0
        for block in self.blocks:
            newPath = os.path.join(path,'block_{}'.format(i))
            os.mkdir(newPath)
            block.write(newPath)
            i += 1