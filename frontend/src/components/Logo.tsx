type LogoProps = {
  size?: number;
  className?: string;
};

// Renderiza o logo SVG da SportFlow inline para não depender de pedidos extras ao servidor, com tamanho configurável e marcado como aria-hidden porque o texto adjacente já comunica a marca a leitores de ecrã.
export function Logo({ size = 36, className }: LogoProps) {
  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 36 36"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      className={className}
      aria-hidden="true"
    >
      {/* Shield base */}
      <path
        d="M18 2L4 8v10c0 7.7 5.9 14.9 14 17 8.1-2.1 14-9.3 14-17V8L18 2z"
        fill="#003ec7"
      />
      {/* Lightning bolt — sport/energy icon */}
      <path
        d="M20.5 7l-6 11h5.5l-2 11 8-13h-6L20.5 7z"
        fill="#fe6b00"
      />
    </svg>
  );
}
