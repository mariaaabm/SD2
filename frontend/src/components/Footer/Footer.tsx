export function Footer() {
  return (
    <footer className="site-footer">
      <div className="site-footer__inner">
        <div>
          <div className="site-footer__brand">Sport<span>Flow</span></div>
          <p className="site-footer__desc">
            A tua loja desportiva online. Equipamento para todos os desportos e niveis, com entrega rapida e precos competitivos.
          </p>
        </div>

        <div className="site-footer__col">
          <h4>Loja</h4>
          <ul>
            <li><a href="/catalog">Catalogo</a></li>
            <li><a href="/catalog?categoryId=1">Calcado</a></li>
            <li><a href="/catalog?categoryId=2">Vestuario</a></li>
            <li><a href="/catalog?categoryId=3">Equipamento</a></li>
          </ul>
        </div>

        <div className="site-footer__col">
          <h4>Conta</h4>
          <ul>
            <li><a href="/login">Entrar</a></li>
            <li><a href="/register">Criar conta</a></li>
            <li><a href="/orders">Historico de compras</a></li>
          </ul>
        </div>

        <div className="site-footer__col">
          <h4>Informacoes</h4>
          <ul>
            <li><a href="#">Sobre nos</a></li>
            <li><a href="#">Politica de privacidade</a></li>
            <li><a href="#">Termos e condicoes</a></li>
          </ul>
        </div>
      </div>

      <div className="site-footer__bottom">
        <span>2024 SportFlow. Todos os direitos reservados.</span>
        <span>Sistemas Distribuidos — Universidade da Beira Interior</span>
      </div>
    </footer>
  );
}
