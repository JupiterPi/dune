from PIL import Image
import os

in_dirname = "./raw"
out_dirname = "./processed"
if not os.path.exists(out_dirname): os.mkdir(out_dirname)

for file in os.listdir(in_dirname):
    print("image: " + file)
    with Image.open(in_dirname + "/" + file) as image:
        (width, height) = image.size
        print(width, height)
        image = image.crop( (1/3*width, 1/3*height, 2/3*width, 2/3*height) )
        image.save(out_dirname + "/" + "processed_" + file)