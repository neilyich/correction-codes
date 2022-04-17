package neilyich.correction.codes.core

import neilyich.correction.codes.core.words.FieldWord
import neilyich.field.element.FieldElement

interface FieldCode<InFieldElement: FieldElement, OutFieldElement: FieldElement>
    : Code<InFieldElement, OutFieldElement, FieldWord<InFieldElement>, FieldWord<OutFieldElement>>
