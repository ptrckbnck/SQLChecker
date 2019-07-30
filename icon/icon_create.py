
from __future__ import unicode_literals
from shutil import copyfile
import svgwrite

def logo_studentgui(name):
    dwg = svgwrite.Drawing(name, (200, 200), debug=True)
    paragraph = dwg.add(dwg.g(font_size=110))
    atext = dwg.text("SQLC", insert=(5, 130),
    style="font-family: Open Sans Condensed;",
    textLength=185)
    paragraph.add(atext)
    dwg.save()

def logo_evalgui(name):
    dwg = svgwrite.Drawing(name, (200, 200), debug=True)
    paragraph = dwg.add(dwg.g(font_size=110))
    atext = dwg.text("SQLC", insert=(5, 120),
    style="font-family: Open Sans Condensed;",
    stroke="white",
    textLength=185)

    paragraph.add(atext)
    paragraph.add(dwg.text("Evaluation", (5, 180),
    style="font-family: Open Sans Condensed;",
    font_size="0.4em",
    fill="blue",
    textLength=185))
    dwg.save()

if __name__ == '__main__':
    logo_studentgui('logo_studentgui.svg')
    logo_evalgui('logo_evalgui.svg')
    copyfile('logo_studentgui.svg','../src/main/resources/images/logo_studentgui.svg')
    copyfile('logo_evalgui.svg','../src/main/resources/images/logo_evalgui.svg')

