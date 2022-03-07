from classes.KLV import KLV

class AccessUnitProtection:
    def __init__(self,dataset_group_ID,dataset_ID,AU_protection_value):
        self.dataset_group_ID = dataset_group_ID
        self.dataset_ID = dataset_ID
        self.AU_protection_value = AU_protection_value
    
    def write(self,path):
        value = self.dataset_group_ID.to_bytes(1,byteorder='little')+self.dataset_ID.to_bytes(2,byteorder='little')+bytes(self.AU_protection_value,'UTF-8')
        klv = KLV('aupr',value)
        klv.write(path)