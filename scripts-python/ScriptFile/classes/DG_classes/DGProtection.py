from classes.KLV import KLV

class DGProtection:
    def __init__(self,dataset_group_ID,DG_prot_value):
        self.dataset_group_ID = dataset_group_ID
        self.DG_prot_value = DG_prot_value
        if self.dataset_group_ID.bit_length() > 8:
            raise ValueError('Invalid dataset_group_ID')

    def write(self,path):
        value = self.dataset_group_ID.to_bytes(1,byteorder='little')+bytes(self.DG_prot_value,'utf-8')
        klv = KLV('dgpr',value)
        klv.write(path)