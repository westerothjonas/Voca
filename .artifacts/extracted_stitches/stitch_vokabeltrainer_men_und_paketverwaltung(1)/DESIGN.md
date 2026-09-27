---
name: Voca Modern Vocabulary
colors:
  surface: '#faf8ff'
  surface-dim: '#d2d9f4'
  surface-bright: '#faf8ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f2f3ff'
  surface-container: '#eaedff'
  surface-container-high: '#e2e7ff'
  surface-container-highest: '#dae2fd'
  on-surface: '#131b2e'
  on-surface-variant: '#434655'
  inverse-surface: '#283044'
  inverse-on-surface: '#eef0ff'
  outline: '#737686'
  outline-variant: '#c3c6d7'
  surface-tint: '#0053db'
  primary: '#004ac6'
  on-primary: '#ffffff'
  primary-container: '#2563eb'
  on-primary-container: '#eeefff'
  inverse-primary: '#b4c5ff'
  secondary: '#006c49'
  on-secondary: '#ffffff'
  secondary-container: '#6cf8bb'
  on-secondary-container: '#00714d'
  tertiary: '#784b00'
  on-tertiary: '#ffffff'
  tertiary-container: '#996100'
  on-tertiary-container: '#ffeedd'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#dbe1ff'
  primary-fixed-dim: '#b4c5ff'
  on-primary-fixed: '#00174b'
  on-primary-fixed-variant: '#003ea8'
  secondary-fixed: '#6ffbbe'
  secondary-fixed-dim: '#4edea3'
  on-secondary-fixed: '#002113'
  on-secondary-fixed-variant: '#005236'
  tertiary-fixed: '#ffddb8'
  tertiary-fixed-dim: '#ffb95f'
  on-tertiary-fixed: '#2a1700'
  on-tertiary-fixed-variant: '#653e00'
  background: '#faf8ff'
  on-background: '#131b2e'
  surface-variant: '#dae2fd'
typography:
  display-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 36px
    fontWeight: '700'
    lineHeight: 44px
    letterSpacing: -0.02em
  display-lg-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 30px
    fontWeight: '700'
    lineHeight: 38px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 28px
    fontWeight: '700'
    lineHeight: 36px
    letterSpacing: -0.015em
  headline-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 22px
    fontWeight: '600'
    lineHeight: 30px
    letterSpacing: -0.01em
  headline-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 26px
  body-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  body-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 18px
  label-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
    letterSpacing: 0.01em
  label-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.02em
  label-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 10px
    fontWeight: '700'
    lineHeight: 14px
    letterSpacing: 0.04em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 1rem
  margin: 1rem
  margin-tablet: 1.5rem
  margin-desktop: 2rem
  space-2xs: 0.25rem
  space-xs: 0.5rem
  space-sm: 0.75rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
  space-2xl: 3rem
---

## Brand & Style

This design system embodies a modern, cognitive-focused vocabulary mastery platform. The aesthetic is crisp, intellectual, and uncluttered, intentionally stripping away ornamental distractions and naturalistic metaphors. In their place, it establishes an environment characterized by clarity, momentum, and functional precision.

### Design Principles
- **Cognitive Clarity:** Eliminate visual noise to prioritize linguistic retention. High typographical contrast against crisp surfaces ensures zero eye fatigue during extended study sessions.
- **Systematic Precision:** Progress and review cycles rely on clean metrics, architectural lines, and structural cards rather than organic analogies.
- **Dynamic Feedback:** Immediate, vibrant accent signals denote acquisition phases, recall success, and memory stabilization with modern clarity.
- **Elevated Minimalism:** Light-mode balance established through subtle border definitions, pure whites, and layered slate neutrals.

## Colors

The palette leverages pure white canvas foundations layered with cool architectural slates, paired with an energetic royal blue primary that anchors active cognitive tasks.

### Functional Usage
- **Primary (`#2563eb`):** Used for key interactive triggers, primary action buttons, active tab indicators, and current word focus states.
- **Secondary / Mastery (`#10b981`):** Represents mastered terms, correct recall actions, accuracy percentages, and positive retention streaks.
- **Tertiary / Progress (`#f59e0b`):** Signals active learning tiers, spaced repetition review queues, and terms requiring reinforcement.
- **Canvas & Surfaces:**
  - Base Background: `#f8fafc`
  - Elevated Container / Flashcard Surface: `#ffffff`
  - Subtle Inset Wells / Recessed Track: `#f1f5f9`
- **Text & Borders:**
  - High-Contrast Primary Text: `#0f172a`
  - Secondary / Supporting Text: `#334155`
  - Muted Label / Hint Text: `#64748b`
  - Hairline Structural Border: `#e2e8f0`
  - Active / Focus Border: `#93c5fd`

## Typography

The typography relies on Plus Jakarta Sans throughout, leveraging its clean geometric proportions, distinct apertures, and high legibility across compact viewports. 

- **Vocabulary Prompts & Lexical Headings:** Formatted with tight negative tracking (`-0.015em` to `-0.02em`) and bold weights to provide instant visual anchoring.
- **Phonetics & Grammatical Notation:** Standardized using `body-md` or `label-md` in secondary slate (`#334155`), ensuring pronunciation markers remain subordinate yet instantly legible.
- **Definitions & Examples:** Typeset in `body-lg` or `body-md` with standard character spacing to optimize reading speed and linguistic comprehension.

## Layout & Spacing

The layout is built around a mobile-first, single-column vertical flow with strict horizontal alignment rules.

### Layout Mechanics
- **Grid Systems:** Single column fluid stack on mobile devices with `1rem` outer canvas padding; transitions to a constrained 480px maximum content width container on larger screens to preserve ergonomic single-hand tap zones.
- **Content Rhythm:** Component separation strictly follows an 8px base rhythm (`space-xs` = 8px, `space-md` = 16px, `space-lg` = 24px).
- **Safe Tap Bounds:** Interactive actions and flashcard interactive regions must respect a minimum touch target size of 48px height with `space-sm` minimum separation between primary controls.

## Elevation & Depth

This system utilizes low-contrast outlines paired with ambient micro-shadows, creating crisp separation without muddiness or heavy Skeuomorphic cues.

### Elevation Levels
- **Base Level (Canvas):** Pure light wash (`#f8fafc`), completely flat, no shadow.
- **Level 1 (Card & Modular Modules):** Crisp white (`#ffffff`) background bordered with 1px `#e2e8f0`, supported by a soft, diffused ambient drop: `box-shadow: 0 1px 3px 0 rgba(15, 23, 42, 0.05), 0 1px 2px -1px rgba(15, 23, 42, 0.05)`.
- **Level 2 (Active Flashcard & Floating Controls):** Pure white with a 1px border (`#e2e8f0`), utilizing an elevated focal shadow: `box-shadow: 0 10px 15px -3px rgba(15, 23, 42, 0.06), 0 4px 6px -4px rgba(15, 23, 42, 0.04)`.
- **Level 3 (Modal Sheets & Contextual Drawers):** `box-shadow: 0 20px 25px -5px rgba(15, 23, 42, 0.08), 0 8px 10px -6px rgba(15, 23, 42, 0.04)`, combined with a 20% opacity neutral slate overlay (`#0f172a33`).

## Shapes

The interface embraces a unified corner language ranging from 12px to 16px (`roundedness: 2`), offering a tactile, approachable quality while maintaining structural balance.

- **Standard Containers & Flashcards:** Standardized at `rounded-lg` (16px / 1rem) for balanced framing of educational content.
- **Buttons, Text Inputs & Selection Cells:** Set to 12px (`0.75rem`) for compact, responsive tactile targets.
- **Status Badges, Chips & Progress Indicators:** Utilize fully rounded pill forms (`rounded-full`) to delineate metadata from core structural cards.

## Components

### Vocabulary Flashcards
- **Structure:** Solid `#ffffff` surface, 1px `#e2e8f0` stroke, 16px corner radius, Level 2 elevation.
- **Content Ordering:** Lexical term (`display-lg-mobile` in `#0f172a`), followed by IPA phonetic rendering (`body-md` in `#334155`), followed by a subtle horizontal divider (`1px solid #f1f5f9`), followed by definition and real-world sample sentences.
- **Flip State:** Clean 3D flip animation with identical structural constraints, displaying semantic context, antonyms/synonyms, and lexical collocations.

### Buttons & Interactive Controls
- **Primary CTA:** Background `#2563eb`, text `#ffffff`, 12px border radius, 48px height, medium weight typography. Active states depress 1px with `#1d4ed8`.
- **Mastered / Success Action:** Background `#10b981`, text `#ffffff`, utilized for positive recall confirmations.
- **Review / Again Action:** Background `#f1f5f9`, text `#334155`, border 1px solid `#e2e8f0`.
- **Ghost Action:** Borderless, text `#2563eb`, hover/press state `#eff6ff`.

### Chips & Badges
- **Status Indicators:** Fully rounded (`rounded-full`), `space-xs` vertical by `space-sm` horizontal padding.
- **State Styling:**
  - *New:* `#eff6ff` fill with `#2563eb` text and border.
  - *Learning:* `#fffbeb` fill with `#b45309` text and `#fde68a` border.
  - *Mastered:* `#ecfdf5` fill with `#047857` text and `#a7f3d0` border.

### Progress & Mastery Metrics
- **Linear Progress Bar:** 8px track height, fully rounded, track background `#f1f5f9`. Active progress bar utilizes `#2563eb` or `#10b981` with smooth transition timing.
- **Metric Tiles:** Level 1 elevation cards displaying raw review metrics, spaced retention intervals, and daily completion statistics cleanly formatted in `headline-md`.

### Input Fields & Search
- **Container:** 48px height, 12px radius, `#ffffff` fill, 1px `#e2e8f0` outline. Focus transitions outline to 2px `#2563eb` with no ambient blur. Placeholder text set in `#64748b`.