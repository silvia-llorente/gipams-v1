from classes.KLV import KLV
from classes.DG_classes.Label import Label

class LabelList:
    def __init__(self,dataset_group_ID):
        self.dataset_group_ID = dataset_group_ID
        self.num_labels = 0
        self.label_list = []
        if self.dataset_group_ID.bit_length() > 8:
            raise ValueError('Invalid dataset_group_ID')

    def addLabel(self,label):
        #No implementat de moment
        if self.num_labels.bit_length() > 16:
            raise ValueError('Too much labels')

        self.label_list.append(label)
        self.num_labels += 1

    def write(self,path):
        value = self.dataset_group_ID.to_bytes(1,byteorder='little')+self.num_labels.to_bytes(2,byteorder='little')
        for label in self.label_list:
            value += label.getValue()

        klv = KLV('labl',value)
        klv.write(path)