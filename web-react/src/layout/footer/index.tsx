import styles from "./footer.module.scss";
export function Footer() {
    return (
        <div className={styles.footer}>
                <span>powered by <a href="https://github.com/securemist">securemist</a></span>
        </div>
    )
}