from classes.FileHeader import FileHeader
from classes.DatasetGroup import DatasetGroup
from classes.Dataset import Dataset 
import os

class MPEG_G:
    def __init__(self):
        self.FileHeader = None
        self.DatasetGroupList = []
        self.AU_count = 0

    def makeFileHeader(self,major_brand,minor_version,compatibleBrand):
        self.FileHeader = FileHeader(major_brand,minor_version,compatibleBrand)

    def addDatasetGroup(self,version_number,DG_metadata_value,DG_prot_value):
        dg = DatasetGroup()
        dg.createDatasetGroup(len(self.DatasetGroupList),version_number,DG_metadata_value,DG_prot_value)
        self.DatasetGroupList.append(dg)

    def createDataset(self):
        return Dataset()

    def addDataset(self,dataset_group_id,dataset):
        if dataset_group_id >= len(self.DatasetGroupList):
            raise ValueError('Invalid dataset group id')
        self.DatasetGroupList[dataset_group_id].addDataset(dataset)
    
    def addAccessUnit(self,dataset_group_id,dataset_id,parameter_set_ID,AU_type,reads_count,AU_information_value,AU_protection_value):
        if dataset_group_id >= len(self.DatasetGroupList):
            raise ValueError('Invalid dataset group id')
        if dataset_id >= len(self.DatasetGroupList[dataset_group_id].datasetList):
            raise ValueError('Invalid dataset id')
        dt = self.DatasetGroupList[dataset_group_id].datasetList[dataset_id]
        access_unit_ID = self.AU_count
        self.AU_count += self.AU_count

        dt.addAccessUnit(access_unit_ID,parameter_set_ID,AU_type,reads_count,AU_information_value,AU_protection_value)
    
    def addBlock(self,dgId,dtId,auId,payload):
        if dgId >= len(self.DatasetGroupList):
            raise ValueError('Invalid dataset group id')
        if dtId >= len(self.DatasetGroupList[dgId].datasetList):
            raise ValueError('Invalid dataset id')
        dt = self.DatasetGroupList[dgId].datasetList[dtId]
        if auId >= len(dt.accessUnits):
            raise ValueError('Invalid Access Unit')
        dt.accessUnits[auId].addBlock(payload)

    def write(self,rootPath):
        os.mkdir(rootPath)
        self.FileHeader.write(rootPath)
        for dg in self.DatasetGroupList:
            path = os.path.join(rootPath,'dg_{}'.format(dg.DatasetGroupHeader.dataset_group_ID))
            os.mkdir(path)
            dg.write(path) 