---
name: SportFlow Energy System
colors:
  surface: '#f8f9fa'
  surface-dim: '#d9dadb'
  surface-bright: '#f8f9fa'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f3f4f5'
  surface-container: '#edeeef'
  surface-container-high: '#e7e8e9'
  surface-container-highest: '#e1e3e4'
  on-surface: '#191c1d'
  on-surface-variant: '#434656'
  inverse-surface: '#2e3132'
  inverse-on-surface: '#f0f1f2'
  outline: '#737688'
  outline-variant: '#c3c5d9'
  surface-tint: '#004ced'
  primary: '#003ec7'
  on-primary: '#ffffff'
  primary-container: '#0052ff'
  on-primary-container: '#dfe3ff'
  inverse-primary: '#b7c4ff'
  secondary: '#a04100'
  on-secondary: '#ffffff'
  secondary-container: '#fe6b00'
  on-secondary-container: '#572000'
  tertiary: '#4e4e4e'
  on-tertiary: '#ffffff'
  tertiary-container: '#666666'
  on-tertiary-container: '#e5e5e5'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#dde1ff'
  primary-fixed-dim: '#b7c4ff'
  on-primary-fixed: '#001452'
  on-primary-fixed-variant: '#0038b6'
  secondary-fixed: '#ffdbcc'
  secondary-fixed-dim: '#ffb693'
  on-secondary-fixed: '#351000'
  on-secondary-fixed-variant: '#7a3000'
  tertiary-fixed: '#e2e2e2'
  tertiary-fixed-dim: '#c6c6c6'
  on-tertiary-fixed: '#1b1b1b'
  on-tertiary-fixed-variant: '#474747'
  background: '#f8f9fa'
  on-background: '#191c1d'
  surface-variant: '#e1e3e4'
typography:
  display-lg:
    fontFamily: Montserrat
    fontSize: 48px
    fontWeight: '800'
    lineHeight: 56px
    letterSpacing: -0.02em
  display-lg-mobile:
    fontFamily: Montserrat
    fontSize: 36px
    fontWeight: '800'
    lineHeight: 42px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Montserrat
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
  headline-md-mobile:
    fontFamily: Montserrat
    fontSize: 24px
    fontWeight: '700'
    lineHeight: 30px
  headline-sm:
    fontFamily: Montserrat
    fontSize: 20px
    fontWeight: '700'
    lineHeight: 28px
  body-lg:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
  body-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  label-bold:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '700'
    lineHeight: 20px
  label-sm:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
spacing:
  base: 4px
  xs: 8px
  sm: 16px
  md: 24px
  lg: 40px
  xl: 64px
  container-max: 1280px
  gutter: 16px
  margin-mobile: 16px
  margin-desktop: 32px
---

## Brand & Style

The design system is engineered for high-performance retail, blending the precision of technical sports gear with the urgency of competitive athletics. The brand personality is **energetic, authoritative, and frictionless**, designed to evoke a sense of momentum and reliability in the user.

The aesthetic follows a **High-Contrast Modern** direction. It utilizes expansive whitespace to allow product photography to breathe, punctuated by aggressive splashes of "Electric Blue" and "Vibrant Orange." To emphasize a technical, "engineered" feel requested for this project, the system avoids soft rounded corners in favor of sharp, precision-cut edges and linear geometry. This approach mirrors the industrial design of modern sports equipment and creates a UI that feels fast, responsive, and durable.

## Colors

The palette is anchored by **Electric Blue (#0052FF)**, used for primary actions and brand presence to signal trust and vitality. **Vibrant Orange (#FF6B00)** serves as the high-visibility accent for "Add to Cart" actions, limited-time offers, and critical conversion points.

Backgrounds utilize a tiered system of **White (#FFFFFF)** for primary content areas and **Light Grey (#F8F9FA)** for structural sections, such as product grids or filters, to create subtle depth without relying on heavy borders. Pure Black is reserved for high-impact typography to ensure maximum readability and a premium feel.

## Typography

This design system employs a dual-font strategy. **Montserrat** is used for headlines and display text to provide a bold, geometric, and "sport-chic" presence. Its heavy weights (700-800) are essential for establishing hierarchy in high-performance marketing layouts.

**Inter** is the workhorse for body copy, labels, and UI elements. Chosen for its exceptional legibility on mobile screens, it ensures that technical specifications and checkout details remain clear. On mobile, display and headline sizes scale down significantly to prevent awkward line breaks while maintaining their distinctive weight.

## Layout & Spacing

The layout is built on a **12-column fluid grid** for desktop and a **4-column grid** for mobile. A strict 4px baseline grid ensures vertical rhythm across all components.

Mobile-first constraints dictate a minimum touch target of 48px for all interactive elements. Content margins are set to 16px on mobile to maximize horizontal space for product cards, expanding to 32px or more on desktop to create a premium, spacious feel. Gutters are kept tight (16px) to maintain the "technical" density of the layout.

## Elevation & Depth

To maintain the sharp, technical aesthetic, elevation is used sparingly. Instead of deep, blurry shadows, the design system utilizes **Short, High-Precision Shadows**.

- **Level 1 (Default):** 1px solid borders in `#E0E0E0` for input fields and product cards.
- **Level 2 (Hover/Lift):** A 4px vertical offset shadow with 12% opacity black, used when a user interacts with a product card.
- **Tonal Depth:** Different shades of grey (`#F8F9FA` vs `#FFFFFF`) are used to separate the navigation and filter sidebars from the main content stream.

## Shapes

In alignment with the "technical" and "sharp" visual direction, the design system utilizes **0px corner radii (Sharp)** for all primary UI components including buttons, cards, and input fields. This creates a distinctive, aggressive look that separates the product from softer, lifestyle-oriented competitors. The only exception to this rule is for status pips or small notification badges where a 2px radius may be applied for optical balance.

## Components

### Buttons
- **Primary:** Electric Blue background, white text, 0px radius. High-impact hover state (slight darken).
- **CTA:** Vibrant Orange background, white text, 0px radius. Used exclusively for "Buy Now" and "Add to Cart."
- **Secondary:** Transparent with a 2px Electric Blue border.

### Product Cards
- Sharp edges, white background.
- Image takes top 60% of card.
- Typography: Montserrat for price (Bold), Inter for product name.
- Subtle Level 2 shadow on hover to indicate interactivity.

### Input Fields
- White background with 1px `#E0E0E0` border.
- Sharp corners.
- On focus: Border changes to 2px Electric Blue.
- Labels: `label-bold` (Inter, 14px, Uppercase) placed above the field.

### Admin Stats & Data Viz
- **Charts:** Use a palette of Electric Blue for primary data and a neutral Slate for secondary data.
- **Data Points:** Sharp-edged "Stats Cards" with `headline-sm` for the primary metric and `label-sm` for the description.
- **Grid Lines:** Minimal, using `#F0F0F0` to keep the interface clean and focused.

### Chips & Badges
- Used for "New Arrival" or "Out of Stock."
- Rectangular with 0px radius.
- High-contrast backgrounds (Black for "New", Red for "Alert").