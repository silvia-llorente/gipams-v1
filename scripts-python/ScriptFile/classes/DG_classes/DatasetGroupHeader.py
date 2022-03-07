from classes.KLV import KLV

class DatasetGroupHeader:
    def __init__(self,dataset_group_ID,version_number):
        self.dataset_group_ID = dataset_group_ID
        self.version_number = version_number
        self.dataset_ID = []
        if self.dataset_group_ID.bit_length() > 8:
            raise ValueError('Invalid dataset_group_ID')
        elif self.version_number.bit_length() > 8:
            raise ValueError('Invalid version_number')

    def addDatasetId(self):
        nextId = len(self.dataset_ID)
        if nextId.bit_length() > 16:
            raise ValueError('Invalid dataset ID')
        self.dataset_ID.append(nextId)
        return nextId

    def write(self,path):
        value = self.dataset_group_ID.to_bytes(1,byteorder='little')+self.version_number.to_bytes(1,byteorder='little')
        for dId in self.dataset_ID:
            value += dId.to_bytes(2,byteorder='little')
        klv = KLV('dghd',value)
        klv.write(path)
