from classes.KLV import KLV

class DTProtection:
    def __init__(self,DG_prot_value):
        self.DG_prot_value = DG_prot_value
    
    def updateIds(self,dataset_group_ID,dataset_ID):
        self.dataset_group_ID = dataset_group_ID
        self.dataset_ID = dataset_ID
        if self.dataset_group_ID.bit_length() > 8:
            raise ValueError('Invalid dataset_group_ID')
        elif self.dataset_ID.bit_length() > 16:
            raise ValueError('Invalid dataset_ID')

    def write(self,path):
        value = self.dataset_group_ID.to_bytes(1,byteorder='little')+self.dataset_ID.to_bytes(2,byteorder='little')+bytes(self.DG_prot_value,'utf-8')
        klv = KLV('dtpr',value)
        klv.write(path)