import { useEffect, useState, type FormEvent } from "react";
import { useAuth } from "../../contexts/AuthContext";
import { useCart } from "../../contexts/CartContext";
import { useWishlist } from "../../contexts/WishlistContext";
import { getProduct, type Product } from "../../services/product.service";
import { getProductReviews, upsertReview, type ProductRatingResponse } from "../../services/review.service";
import { getCategoryBg, getCategoryColor, getCategoryIcon } from "../../utils/categoryUtils";

type ProductPageProps = { productId: number };

export function ProductPage({ productId }: ProductPageProps) {
  const { addProduct } = useCart();
  const { isAuthenticated } = useAuth();
  const { toggle, isWishlisted } = useWishlist();
  const [product, setProduct] = useState<Product | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [added, setAdded] = useState(false);

  const [ratings, setRatings] = useState<ProductRatingResponse | null>(null);
  const [userRating, setUserRating] = useState(0);
  const [hoverRating, setHoverRating] = useState(0);
  const [comment, setComment] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [reviewError, setReviewError] = useState<string | null>(null);

  useEffect(() => {
    getProduct(productId)
      .then(setProduct)
      .catch(() => setError("Nao foi possivel carregar o produto."))
      .finally(() => setLoading(false));
  }, [productId]);

  useEffect(() => {
    getProductReviews(productId).then(setRatings).catch(() => {});
  }, [productId]);

  if (loading) return <div className="status-message">A carregar produto...</div>;
  if (error || !product) return <div className="status-message status-message--error">{error || "Produto nao encontrado."}</div>;

  const available = product.active && product.stock > 0;

  function handleAdd() {
    if (!product) return;
    addProduct(product);
    setAdded(true);
    setTimeout(() => setAdded(false), 2000);
  }

  async function handleSubmitReview(e: FormEvent) {
    e.preventDefault();
    if (userRating === 0) { setReviewError("Seleciona uma classificacao."); return; }
    setSubmitting(true);
    setReviewError(null);
    try {
      await upsertReview(productId, userRating, comment.trim());
      const updated = await getProductReviews(productId);
      setRatings(updated);
      setComment("");
      setUserRating(0);
    } catch {
      setReviewError("Erro ao submeter avaliacao. Tenta novamente.");
    } finally {
      setSubmitting(false);
    }
  }

  const wishlisted = isWishlisted(product.id);

  const displayRating = hoverRating || userRating;

  return (
    <section className="product-page">
      <a className="secondary-link" href="/catalog">← Voltar ao catalogo</a>

      <div className="product-detail">
        <div className="product-detail__image-wrap" style={{ position: "relative" }}>
          {product.imageUrl ? (
            <img src={product.imageUrl} alt={product.name} />
          ) : (
            <div
              className="product-detail__placeholder"
              style={{ background: getCategoryBg(product.categoryName) }}
            >
              <span style={{ fontWeight: 900, color: getCategoryColor(product.categoryName) }}>
                {getCategoryIcon(product.categoryName)}
              </span>
            </div>
          )}
          {isAuthenticated && (
            <button
              type="button"
              className={`btn-wishlist btn-wishlist--lg${wishlisted ? " btn-wishlist--active" : ""}`}
              title={wishlisted ? "Remover dos favoritos" : "Adicionar aos favoritos"}
              onClick={() => toggle(product.id)}
            >
              {wishlisted ? "♥" : "♡"}
            </button>
          )}
        </div>

        <div className="product-detail__info">
          <span className="product-detail__category">{product.categoryName}</span>
          <h1 className="product-detail__name">{product.name}</h1>

          {ratings && ratings.count > 0 && (
            <div className="product-detail__rating">
              {"★".repeat(Math.round(ratings.average))}{"☆".repeat(5 - Math.round(ratings.average))}
              <span> {ratings.average.toFixed(1)} ({ratings.count} {ratings.count === 1 ? "avaliacao" : "avaliacoes"})</span>
            </div>
          )}

          {product.description && (
            <p className="product-detail__desc">{product.description}</p>
          )}

          <div className="product-detail__price">
            {product.price.toFixed(2)} €
            <span> / unidade</span>
          </div>

          <p className="product-detail__stock">
            {available
              ? <><strong>{product.stock}</strong> unidades em stock</>
              : <span style={{ color: "#dc2626" }}>Produto indisponivel</span>}
          </p>

          <div className="action-row" style={{ marginTop: 8 }}>
            <button
              type="button"
              className="btn-add"
              style={{ height: 48, padding: "0 28px", fontSize: 15 }}
              disabled={!available}
              onClick={handleAdd}
            >
              {added ? "Adicionado!" : available ? "+ Adicionar ao carrinho" : "Indisponivel"}
            </button>
            <a className="primary-link" href="/cart">Ver carrinho</a>
          </div>
        </div>
      </div>

      <div className="reviews-section">
        <h2 className="reviews-section__title">Avaliacoes</h2>

        {ratings && ratings.count > 0 && (
          <div className="reviews-summary">
            <div className="reviews-summary__avg">{ratings.average.toFixed(1)}</div>
            <div>
              <div className="reviews-summary__stars">
                {[1,2,3,4,5].map((s) => (
                  <span key={s} className={s <= Math.round(ratings.average) ? "star star--filled" : "star"}>★</span>
                ))}
              </div>
              <div className="reviews-summary__count">{ratings.count} {ratings.count === 1 ? "avaliacao" : "avaliacoes"}</div>
            </div>
          </div>
        )}

        {isAuthenticated ? (
          <form className="review-form" onSubmit={handleSubmitReview}>
            <p className="review-form__label">A tua avaliacao</p>
            <div className="star-picker">
              {[1,2,3,4,5].map((s) => (
                <button
                  key={s}
                  type="button"
                  className={`star-picker__star${s <= displayRating ? " star-picker__star--active" : ""}`}
                  onMouseEnter={() => setHoverRating(s)}
                  onMouseLeave={() => setHoverRating(0)}
                  onClick={() => setUserRating(s)}
                  aria-label={`${s} estrelas`}
                >★</button>
              ))}
            </div>
            <textarea
              className="review-form__textarea"
              placeholder="Escreve um comentario (opcional)..."
              value={comment}
              onChange={(e) => setComment(e.target.value)}
              maxLength={1000}
              rows={3}
            />
            {reviewError && <p className="review-form__error">{reviewError}</p>}
            <button type="submit" className="btn-add" disabled={submitting}>
              {submitting ? "A enviar..." : "Publicar avaliacao"}
            </button>
          </form>
        ) : (
          <p className="reviews-section__login-prompt">
            <a href="/login">Entra na tua conta</a> para deixar uma avaliacao.
          </p>
        )}

        {ratings && ratings.reviews.length > 0 ? (
          <div className="reviews-list">
            {ratings.reviews.map((r) => (
              <div key={r.id} className="review-card">
                <div className="review-card__header">
                  <strong className="review-card__author">{r.customerName}</strong>
                  <span className="review-card__stars">
                    {"★".repeat(r.rating)}{"☆".repeat(5 - r.rating)}
                  </span>
                  <span className="review-card__date">{new Date(r.createdAt).toLocaleDateString("pt-PT")}</span>
                </div>
                {r.comment && <p className="review-card__comment">{r.comment}</p>}
              </div>
            ))}
          </div>
        ) : (
          ratings && ratings.count === 0 && (
            <p className="reviews-section__empty">Ainda nao ha avaliacoes para este produto.</p>
          )
        )}
      </div>
    </section>
  );
}
