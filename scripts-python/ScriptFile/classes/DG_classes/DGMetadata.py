from classes.KLV import KLV

class DGMetadata:
    def __init__(self,dataset_group_ID,DG_metadata_value):
        self.dataset_group_ID = dataset_group_ID
        self.DG_metadata_value = DG_metadata_value
        if self.dataset_group_ID.bit_length() > 8:
            raise ValueError('Invalid dataset_group_ID')

    def write(self,path):
        value = self.dataset_group_ID.to_bytes(1,byteorder='little')+bytes(self.DG_metadata_value,'utf-8')
        klv = KLV('dgmd',value)
        klv.write(path)
