import shutil
import sys

colors = {
    "white",
    "orange",
    "magenta",
    "light_blue",
    "yellow",
    "lime",
    "pink",
    "gray",
    "light_gray",
    "cyan",
    "purple",
    "blue",
    "brown",
    "green",
    "red",
    "black"
}

shades = {
    "",
    "shaded_"
}

paths = {
    "block",
    "item"
}

names = {
    "button",
    "clear_full",
    "flat_fixture",
    "large_fixture",
    "medium_fixture",
    "reinforced_fixture",
    "reinforced_flat_fixture",
    "reinforced_large_fixture",
    "reinforced_medium_fixture",
    "reinforced_small_fixture",
    "small_fixture",
    "switch"
}

for shade in shades:
    for color in colors:
        for path in paths:
            for name in names:
                shutil.copyfile("./input/" + path + "/" + name + ".json", "./output/" + shade + name + "_" + color + ".json")
