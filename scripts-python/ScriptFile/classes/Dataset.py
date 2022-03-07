from classes.DT_classes.DatasetHeader import DatasetHeader
from classes.DT_classes.DTMetadata import DTMetadata
from classes.DT_classes.DTProtection import DTProtection
#from classes.DT_classes.DatasetParamSet import DatasetParamSet
from classes.AccessUnit import AccessUnit
import os

class Dataset:
    def __init__(self):
        self.DatasetHeader = None
        self.DTMetadata = None
        self.DTProtection = None
        #self.datasetParamSet = []
        self.accessUnits = []

    def addHeader(self,version,dataset_type,alphabet_ID):
        self.DatasetHeader = DatasetHeader(version,dataset_type,alphabet_ID)

    def addMetadata(self,DT_metadata_value):
        self.DTMetadata = DTMetadata(DT_metadata_value)
        
    def addProtection(self,DG_prot_value):
        self.DTProtection = DTProtection(DG_prot_value)
    
    #def addDatasetParamSet(self,multiple_alignment_flag):
    #    parameter_set_ID = len(datasetParamSet)
    #    parent_parameter_set_ID = 0
        

    def updateIds(self,datasetGroupID,datasetID):
        self.DatasetHeader.updateIds(datasetGroupID,datasetID)
        try:
            self.DTMetadata.updateIds(datasetGroupID,datasetID)
        except:
            pass
        try: 
            self.DTProtection.updateIds(datasetGroupID,datasetID)
        except:
            pass
    
    def addAccessUnit(self,access_unit_ID,parameter_set_ID,AU_type,reads_count,AU_information_value,AU_protection_value):
        au = AccessUnit()
        au.addHeader(access_unit_ID,parameter_set_ID,AU_type,reads_count)
        dgId = self.DatasetHeader.dataset_group_ID
        dtId = self.DatasetHeader.dataset_ID
        au.addInformation(dgId,dtId,AU_information_value)
        au.addProtection(dgId,dtId,AU_protection_value)
        self.accessUnits.append(au)

    def write(self,path):
        for item in self.__dict__.items():
            if not item[0][0].islower() and item[1] != None:
                item[1].write(path)
        i = 0
        for au in self.accessUnits:
            newPath = os.path.join(path,'au_{}'.format(i))
            os.mkdir(newPath)
            au.write(newPath)
            i += 1