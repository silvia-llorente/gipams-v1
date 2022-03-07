from classes.KLV import KLV

class AccessUnitInformation:
    def __init__(self,dataset_group_ID,dataset_ID,AU_information_value):
        self.dataset_group_ID = dataset_group_ID
        self.dataset_ID = dataset_ID
        self.AU_information_value = AU_information_value
    
    def write(self,path):
        value = self.dataset_group_ID.to_bytes(1,byteorder='little')+self.dataset_ID.to_bytes(2,byteorder='little')+bytes(self.AU_information_value,'UTF-8')
        klv = KLV('auin',value)
        klv.write(path)



