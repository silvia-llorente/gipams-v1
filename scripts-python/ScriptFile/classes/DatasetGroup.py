from classes.DG_classes.DatasetGroupHeader import DatasetGroupHeader
from classes.DG_classes.LabelList import LabelList
from classes.DG_classes.DGMetadata import DGMetadata
from classes.DG_classes.DGProtection import DGProtection
from classes.Dataset import Dataset
import os

class DatasetGroup:
    def __init__(self):
        self.DatasetGroupHeader = None
        self.Reference = None
        self.ReferenceMetadata = None
        self.LabelList = None
        self.DGMetadata = None
        self.DGProtection = None
        self.datasetList = []

    def createDatasetGroup(self,dataset_group_ID,version_number,DG_metadata_value,DG_prot_value):
        self.DatasetGroupHeader = DatasetGroupHeader(dataset_group_ID,version_number)
        #Opcionals, no implementats
        #self.Reference = DatasetGroupHeader(dataset_group_ID,version_number,[])
        #self.ReferenceMetadata = DatasetGroupHeader(dataset_group_ID,version_number,[])
        self.LabelList = LabelList(dataset_group_ID)
        self.DGMetadata = DGMetadata(dataset_group_ID,DG_metadata_value)
        self.DGProtection = DGProtection(dataset_group_ID,DG_prot_value)

    def addDataset(self,dataset):
        newId = self.DatasetGroupHeader.addDatasetId()
        dataset.updateIds(self.DatasetGroupHeader.dataset_group_ID,newId)
        self.datasetList.append(dataset)

    def write(self,rootPath):
        for item in self.__dict__.items():
            if not item[0][0].islower() and item[1] != None:
                item[1].write(rootPath)
    
        for dt in self.datasetList:
            path = os.path.join(rootPath,'dt_{}'.format(dt.DatasetHeader.dataset_ID))
            os.mkdir(path)
            dt.write(path)