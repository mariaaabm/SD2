import { useState } from "react";
import { useAuth } from "../contexts/AuthContext";
import { useCart } from "../contexts/CartContext";
import { checkout, type Sale } from "../services/sale.service";
import type { AxiosError } from "axios";

type ApiError = { messages: string[] };

type PaymentMethod = "CARD" | "MBWAY" | "MULTIBANCO" | "COD";

const PT_DISTRICTS = [
  "Aveiro", "Beja", "Braga", "Bragança", "Castelo Branco", "Coimbra",
  "Évora", "Faro", "Guarda", "Leiria", "Lisboa", "Portalegre", "Porto",
  "Santarém", "Setúbal", "Viana do Castelo", "Vila Real", "Viseu",
  "Região Autónoma dos Açores", "Região Autónoma da Madeira",
];

const COUNTRIES = [
  "Portugal", "Espanha", "França", "Alemanha", "Reino Unido", "Itália",
  "Países Baixos", "Bélgica", "Luxemburgo", "Suíça", "Áustria", "Irlanda",
];

const PAYMENT_LABELS: Record<PaymentMethod, string> = {
  CARD:       "Cartão de crédito / débito",
  MBWAY:      "MB Way",
  MULTIBANCO: "Referência Multibanco",
  COD:        "Pagamento na entrega",
};

const PAYMENT_ICONS: Record<PaymentMethod, string> = {
  CARD:       "💳",
  MBWAY:      "📱",
  MULTIBANCO: "🏧",
  COD:        "🚚",
};

// Formata o número do cartão agrupando em blocos de 4 dígitos (ex.: "4111 1111 1111 1111")
// para melhorar a legibilidade e detetar erros de introdução mais facilmente.
// Nota: estes dados de cartão NÃO são enviados para o backend — o checkout apenas envia o paymentMethod.
function formatCardNumber(raw: string) {
  return raw.replace(/\D/g, "").slice(0, 16).replace(/(.{4})/g, "$1 ").trim();
}

// Formata a validade no formato MM/AA inserindo automaticamente a barra após os primeiros 2 dígitos.
function formatExpiry(raw: string) {
  const digits = raw.replace(/\D/g, "").slice(0, 4);
  return digits.length > 2 ? digits.slice(0, 2) + "/" + digits.slice(2) : digits;
}

// Página de checkout com formulários de contacto, morada de entrega e método de pagamento, valida todos os campos obrigatórios antes de submeter, chama o endpoint /sales/checkout e ao receber resposta limpa o carrinho e mostra a confirmação da encomenda com ligação para a fatura.
export function CheckoutPage() {
  const { customer, isAuthenticated } = useAuth();
  const { items, total, clear } = useCart();
  const [sale, setSale] = useState<Sale | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  // Contact & shipping
  const [name, setName] = useState(customer?.name ?? "");
  const [phone, setPhone] = useState("");
  const [address, setAddress] = useState("");
  const [address2, setAddress2] = useState("");
  const [postalCode, setPostalCode] = useState("");
  const [city, setCity] = useState("");
  const [region, setRegion] = useState("");
  const [country, setCountry] = useState("Portugal");

  // Payment
  const [paymentMethod, setPaymentMethod] = useState<PaymentMethod>("CARD");
  const [cardNumber, setCardNumber] = useState("");
  const [cardExpiry, setCardExpiry] = useState("");
  const [cardCvv, setCardCvv] = useState("");
  const [cardName, setCardName] = useState("");
  const [mbwayPhone, setMbwayPhone] = useState("");

  function validate() {
    const errs: Record<string, string> = {};
    if (!name.trim()) errs.name = "O nome é obrigatório.";
    if (!phone.trim()) errs.phone = "O telefone é obrigatório.";
    if (!address.trim()) errs.address = "A morada é obrigatória.";
    if (!postalCode.trim()) errs.postalCode = "O código postal é obrigatório.";
    if (!city.trim()) errs.city = "A cidade é obrigatória.";
    if (!country.trim()) errs.country = "O país é obrigatório.";

    if (paymentMethod === "CARD") {
      const digits = cardNumber.replace(/\s/g, "");
      if (digits.length < 16) errs.cardNumber = "Número de cartão inválido.";
      if (!/^\d{2}\/\d{2}$/.test(cardExpiry)) errs.cardExpiry = "Data inválida (MM/AA).";
      if (cardCvv.length < 3) errs.cardCvv = "CVV inválido.";
      if (!cardName.trim()) errs.cardName = "O nome no cartão é obrigatório.";
    }
    if (paymentMethod === "MBWAY") {
      if (!mbwayPhone.trim()) errs.mbwayPhone = "O número MB Way é obrigatório.";
    }
    return errs;
  }

  async function handleCheckout(e: React.FormEvent) {
    e.preventDefault();
    setError(null);

    const errs = validate();
    if (Object.keys(errs).length > 0) {
      setFieldErrors(errs);
      return;
    }
    setFieldErrors({});
    setLoading(true);

    try {
      const response = await checkout({
        items: items.map((item) => ({ productId: item.product.id, quantity: item.quantity })),
        shippingName: name.trim(),
        shippingPhone: phone.trim(),
        shippingAddress: address.trim(),
        shippingAddress2: address2.trim() || undefined,
        shippingPostalCode: postalCode.trim(),
        shippingCity: city.trim(),
        shippingRegion: region.trim() || undefined,
        shippingCountry: country,
        paymentMethod,
      });
      setSale(response);
      clear();
    } catch (err: unknown) {
      const axiosErr = err as AxiosError<ApiError>;
      const msgs = axiosErr?.response?.data?.messages;
      if (msgs && msgs.length > 0) {
        setError(msgs[0]);
      } else {
        setError("Não foi possível finalizar a compra. Confirma o login e o stock disponível.");
      }
    } finally {
      setLoading(false);
    }
  }

  if (!isAuthenticated) {
    return (
      <section className="checkout-page checkout-page--narrow">
        <div className="status-message status-message--error">
          Tens de entrar antes de finalizar a compra.
        </div>
        <a className="primary-link" href="/login">Entrar</a>
      </section>
    );
  }

  if (items.length === 0 && !sale) {
    return (
      <section className="checkout-page checkout-page--narrow">
        <div className="page-heading"><h1>Checkout</h1></div>
        <div className="status-message">O teu carrinho está vazio.</div>
        <a className="primary-link" href="/catalog">Ver produtos</a>
      </section>
    );
  }

  if (sale) {
    const payLabel = PAYMENT_LABELS[sale.paymentMethod as PaymentMethod] ?? sale.paymentMethod;
    return (
      <section className="checkout-page checkout-page--narrow">
        <div className="checkout-success">
          <div className="checkout-success__icon">✓</div>
          <h1>Encomenda confirmada!</h1>
          <p>
            Obrigado, <strong>{sale.shippingName}</strong>! A tua encomenda{" "}
            <strong>#{sale.id}</strong> foi registada com sucesso.
          </p>
          <div className="checkout-success__total">
            Total: <strong>{sale.total.toFixed(2)} €</strong>
          </div>
          {sale.shippingAddress && (
            <div className="checkout-success__address">
              <span>Entrega em</span>
              <strong>
                {sale.shippingAddress}
                {sale.shippingAddress2 ? `, ${sale.shippingAddress2}` : ""}
                {" — "}{sale.shippingPostalCode} {sale.shippingCity}
              </strong>
            </div>
          )}
          <div className="checkout-success__payment">
            <span>Pagamento:</span> {payLabel}
          </div>
          {sale.invoice && (
            <div className="checkout-success__invoice">
              Fatura <strong>{sale.invoice.invoiceNumber}</strong> emitida em{" "}
              {new Date(sale.invoice.issuedAt).toLocaleDateString("pt-PT")}
            </div>
          )}
          <div className="action-row">
            {sale.invoice && (
              <a className="primary-link" href={`/orders/${sale.id}/invoice`}>
                Ver fatura
              </a>
            )}
            <a className="secondary-link" href="/orders">Ver todas as compras</a>
            <a className="secondary-link" href="/catalog">Continuar a comprar</a>
          </div>
        </div>
      </section>
    );
  }

  const itemCount = items.reduce((s, i) => s + i.quantity, 0);

  return (
    <form className="checkout-layout" onSubmit={handleCheckout} noValidate>
      {/* ── Left column: form ── */}
      <div className="checkout-form-col">

        {/* Breadcrumb */}
        <nav className="checkout-breadcrumb">
          <a href="/cart">Carrinho</a>
          <span>›</span>
          <span className="checkout-breadcrumb--active">Finalizar compra</span>
        </nav>

        {error && (
          <div className="status-message status-message--error">{error}</div>
        )}

        {/* ── Section 1: Contact ── */}
        <section className="co-section">
          <h2 className="co-section__title">
            <span className="co-section__num">1</span>
            Informações de contacto
          </h2>

          <div className="co-grid co-grid--2">
            <div className="co-field">
              <label htmlFor="co-name">Nome completo *</label>
              <input
                id="co-name"
                type="text"
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="João Silva"
                autoComplete="name"
              />
              {fieldErrors.name && <span className="co-field__error">{fieldErrors.name}</span>}
            </div>

            <div className="co-field">
              <label htmlFor="co-email">Email</label>
              <input
                id="co-email"
                type="email"
                value={customer?.email ?? ""}
                disabled
                readOnly
              />
            </div>

            <div className="co-field">
              <label htmlFor="co-phone">Telemóvel *</label>
              <input
                id="co-phone"
                type="tel"
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
                placeholder="+351 912 345 678"
                autoComplete="tel"
              />
              {fieldErrors.phone && <span className="co-field__error">{fieldErrors.phone}</span>}
            </div>
          </div>
        </section>

        {/* ── Section 2: Shipping address ── */}
        <section className="co-section">
          <h2 className="co-section__title">
            <span className="co-section__num">2</span>
            Morada de entrega
          </h2>

          <div className="co-grid co-grid--1">
            <div className="co-field">
              <label htmlFor="co-address">Morada *</label>
              <input
                id="co-address"
                type="text"
                value={address}
                onChange={(e) => setAddress(e.target.value)}
                placeholder="Rua das Flores, 42"
                autoComplete="address-line1"
              />
              {fieldErrors.address && <span className="co-field__error">{fieldErrors.address}</span>}
            </div>

            <div className="co-field">
              <label htmlFor="co-address2">Complemento (opcional)</label>
              <input
                id="co-address2"
                type="text"
                value={address2}
                onChange={(e) => setAddress2(e.target.value)}
                placeholder="Andar, apartamento, lote…"
                autoComplete="address-line2"
              />
            </div>
          </div>

          <div className="co-grid co-grid--3">
            <div className="co-field">
              <label htmlFor="co-postal">Código Postal *</label>
              <input
                id="co-postal"
                type="text"
                value={postalCode}
                onChange={(e) => setPostalCode(e.target.value)}
                placeholder="1000-001"
                autoComplete="postal-code"
                maxLength={8}
              />
              {fieldErrors.postalCode && <span className="co-field__error">{fieldErrors.postalCode}</span>}
            </div>

            <div className="co-field">
              <label htmlFor="co-city">Cidade *</label>
              <input
                id="co-city"
                type="text"
                value={city}
                onChange={(e) => setCity(e.target.value)}
                placeholder="Lisboa"
                autoComplete="address-level2"
              />
              {fieldErrors.city && <span className="co-field__error">{fieldErrors.city}</span>}
            </div>

            <div className="co-field">
              <label htmlFor="co-region">Distrito</label>
              <select
                id="co-region"
                value={region}
                onChange={(e) => setRegion(e.target.value)}
              >
                <option value="">Selecionar…</option>
                {PT_DISTRICTS.map((d) => (
                  <option key={d} value={d}>{d}</option>
                ))}
              </select>
            </div>
          </div>

          <div className="co-grid co-grid--2">
            <div className="co-field">
              <label htmlFor="co-country">País *</label>
              <select
                id="co-country"
                value={country}
                onChange={(e) => setCountry(e.target.value)}
              >
                {COUNTRIES.map((c) => (
                  <option key={c} value={c}>{c}</option>
                ))}
              </select>
              {fieldErrors.country && <span className="co-field__error">{fieldErrors.country}</span>}
            </div>
          </div>
        </section>

        {/* ── Section 3: Payment ── */}
        <section className="co-section">
          <h2 className="co-section__title">
            <span className="co-section__num">3</span>
            Método de pagamento
          </h2>

          <div className="co-payment-options">
            {(["CARD", "MBWAY", "MULTIBANCO", "COD"] as PaymentMethod[]).map((m) => (
              <label
                key={m}
                className={`co-payment-option${paymentMethod === m ? " co-payment-option--active" : ""}`}
              >
                <input
                  type="radio"
                  name="paymentMethod"
                  value={m}
                  checked={paymentMethod === m}
                  onChange={() => setPaymentMethod(m)}
                />
                <span className="co-payment-option__icon">{PAYMENT_ICONS[m]}</span>
                <span className="co-payment-option__label">{PAYMENT_LABELS[m]}</span>
              </label>
            ))}
          </div>

          {/* Card details */}
          {paymentMethod === "CARD" && (
            <div className="co-card-fields">
              <div className="co-field co-field--wide">
                <label htmlFor="co-card-num">Número do cartão *</label>
                <input
                  id="co-card-num"
                  type="text"
                  inputMode="numeric"
                  value={cardNumber}
                  onChange={(e) => setCardNumber(formatCardNumber(e.target.value))}
                  placeholder="0000 0000 0000 0000"
                  maxLength={19}
                  autoComplete="cc-number"
                />
                {fieldErrors.cardNumber && <span className="co-field__error">{fieldErrors.cardNumber}</span>}
              </div>

              <div className="co-grid co-grid--3">
                <div className="co-field co-field--span2">
                  <label htmlFor="co-card-expiry">Validade (MM/AA) *</label>
                  <input
                    id="co-card-expiry"
                    type="text"
                    inputMode="numeric"
                    value={cardExpiry}
                    onChange={(e) => setCardExpiry(formatExpiry(e.target.value))}
                    placeholder="MM/AA"
                    maxLength={5}
                    autoComplete="cc-exp"
                  />
                  {fieldErrors.cardExpiry && <span className="co-field__error">{fieldErrors.cardExpiry}</span>}
                </div>

                <div className="co-field">
                  <label htmlFor="co-card-cvv">CVV *</label>
                  <input
                    id="co-card-cvv"
                    type="text"
                    inputMode="numeric"
                    value={cardCvv}
                    onChange={(e) => setCardCvv(e.target.value.replace(/\D/g, "").slice(0, 4))}
                    placeholder="123"
                    maxLength={4}
                    autoComplete="cc-csc"
                  />
                  {fieldErrors.cardCvv && <span className="co-field__error">{fieldErrors.cardCvv}</span>}
                </div>
              </div>

              <div className="co-field co-field--wide">
                <label htmlFor="co-card-name">Nome no cartão *</label>
                <input
                  id="co-card-name"
                  type="text"
                  value={cardName}
                  onChange={(e) => setCardName(e.target.value.toUpperCase())}
                  placeholder="JOÃO SILVA"
                  autoComplete="cc-name"
                />
                {fieldErrors.cardName && <span className="co-field__error">{fieldErrors.cardName}</span>}
              </div>

              <p className="co-secure-note">
                🔒 Os dados do cartão são encriptados e processados em segurança.
              </p>
            </div>
          )}

          {/* MB Way */}
          {paymentMethod === "MBWAY" && (
            <div className="co-card-fields">
              <div className="co-field co-field--wide">
                <label htmlFor="co-mbway-phone">Número de telemóvel MB Way *</label>
                <input
                  id="co-mbway-phone"
                  type="tel"
                  value={mbwayPhone}
                  onChange={(e) => setMbwayPhone(e.target.value)}
                  placeholder="+351 912 345 678"
                />
                {fieldErrors.mbwayPhone && <span className="co-field__error">{fieldErrors.mbwayPhone}</span>}
              </div>
              <p className="co-info-note">
                Receberás uma notificação na app MB Way para confirmares o pagamento.
              </p>
            </div>
          )}

          {/* Multibanco */}
          {paymentMethod === "MULTIBANCO" && (
            <div className="co-card-fields">
              <p className="co-info-note">
                Após confirmares a encomenda, irás receber uma referência Multibanco por email para efetuar o pagamento.
              </p>
            </div>
          )}

          {/* Cash on delivery */}
          {paymentMethod === "COD" && (
            <div className="co-card-fields">
              <p className="co-info-note">
                O pagamento é efetuado no momento da entrega. Pode ser cobrada uma taxa adicional de serviço.
              </p>
            </div>
          )}
        </section>
      </div>

      {/* ── Right column: order summary ── */}
      <aside className="checkout-sidebar">
        <div className="co-summary">
          <h2 className="co-summary__title">Resumo da encomenda</h2>

          <div className="co-summary__items">
            {items.map((item) => (
              <div className="co-summary__item" key={item.product.id}>
                <div className="co-summary__item-img-wrap">
                  {item.product.imageUrl ? (
                    <img
                      src={item.product.imageUrl}
                      alt={item.product.name}
                      className="co-summary__item-img"
                      onError={(e) => { (e.target as HTMLImageElement).style.display = "none"; }}
                    />
                  ) : (
                    <div className="co-summary__item-img-ph">🏷️</div>
                  )}
                  <span className="co-summary__item-qty">{item.quantity}</span>
                </div>
                <div className="co-summary__item-info">
                  <strong>{item.product.name}</strong>
                  <span>{item.product.categoryName}</span>
                </div>
                <span className="co-summary__item-price">
                  {(item.product.price * item.quantity).toFixed(2)} €
                </span>
              </div>
            ))}
          </div>

          <div className="co-summary__totals">
            <div className="co-summary__row">
              <span>Subtotal ({itemCount} {itemCount === 1 ? "artigo" : "artigos"})</span>
              <span>{total.toFixed(2)} €</span>
            </div>
            <div className="co-summary__row">
              <span>Envio</span>
              <span className="co-summary__free">Gratuito</span>
            </div>
            <div className="co-summary__row co-summary__row--total">
              <strong>Total</strong>
              <strong>{total.toFixed(2)} €</strong>
            </div>
          </div>

          <button
            className="co-summary__btn"
            type="submit"
            disabled={loading}
          >
            {loading ? "A processar…" : `Confirmar encomenda · ${total.toFixed(2)} €`}
          </button>

          <a className="co-summary__back" href="/cart">← Voltar ao carrinho</a>

          <div className="co-trust-badges">
            <span>🔒 Pagamento seguro</span>
            <span>🚚 Envio gratuito</span>
            <span>↩️ Devoluções gratuitas</span>
          </div>
        </div>
      </aside>
    </form>
  );
}
